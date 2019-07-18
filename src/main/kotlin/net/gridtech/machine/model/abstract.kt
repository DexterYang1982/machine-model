package net.gridtech.machine.model

import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Single
import net.gridtech.core.data.*
import net.gridtech.core.util.APIExceptionEnum
import net.gridtech.core.util.cast
import net.gridtech.core.util.compose
import net.gridtech.core.util.stringfy
import net.gridtech.machine.model.entityField.CustomField
import net.gridtech.machine.model.property.field.ValueDescription

abstract class IBaseStructure<T : IStructureData>(val id: String) {
    open fun initialize(initData: T?) {
        source = initData
    }

    open val description: IBaseProperty<*, T>? = null

    val name = object : IBaseProperty<String, IStructureData>({ structure ->
        structure.name
    }) {}
    val alias = object : IBaseProperty<String, IStructureData>({ structure ->
        structure.alias
    }) {}

    var source: T? = null
        set(v) {
            if (v != null) {
                field = v
                name.source = v
                alias.source = v
                description?.source = v
            }
        }

    abstract fun update(name: String, alias: String, description: Any?)
    abstract fun dataType(): String
    open fun dataName(): String = javaClass.simpleName
    abstract fun parentId(): String?
    abstract fun nodeClassId(): String?
    open fun path(): List<String> = emptyList()

    fun capsule() = StructureDataUpdateCapsule(
            id = id,
            dataName = dataName(),
            dataType = dataType(),
            updateType = "update",
            content = stringfy(mapOf(
                    "id" to id,
                    "name" to name.value,
                    "alias" to alias.value,
                    "description" to description?.value,
                    "nodeClassId" to nodeClassId(),
                    "parentId" to parentId(),
                    "path" to path()
            )),
            updateTime = source?.updateTime ?: -1
    )

    fun updateNameAndAlias(name: String, alias: String) =
            source?.let {
                update(name, alias, description?.value)
            }

    fun updateDescription(description: Any?) =
            source?.let {
                update(it.name, it.alias, description)
            }

    fun delete() {
        name.delete()
        alias.delete()
        description?.delete()
    }

    fun onDelete(): Single<*> = DataHolder.instance.structureDataChangedPublisher
            .filter { (type, structure) ->
                type == StructureDataChangedType.DELETE && structure.id == id
            }.firstOrError()

    abstract fun doDelete()

    fun tryToDelete(): Boolean =
            source?.let {
                APIExceptionEnum.ERR10_CAN_NOT_BE_DELETED.assert(DataHolder.instance.checkDependency(id))
                doDelete()
                true
            } ?: false
}

abstract class IEntityClass(id: String) : IBaseStructure<INodeClass>(id) {
    val embeddedFields = ArrayList<IEmbeddedEntityField<*>>()
    override fun dataType(): String = "EntityClass"
    override fun parentId(): String? = null
    override fun nodeClassId(): String? = null
    override fun initialize(initData: INodeClass?) {
        javaClass.methods.filter { method ->
            method.name.startsWith("get") && method.returnType.superclass == IEmbeddedEntityField::class.java
        }.forEach { method ->
            embeddedFields.add(method.invoke(this) as IEmbeddedEntityField<*>)
        }
        if (initData != null) {
            embeddedFields.forEach { field ->
                DataHolder.instance.entityFieldHolder[field.id] = field
            }
        }
        super.initialize(initData)
    }


    protected fun addNew(name: String, alias: String, tags: List<String>, connectable: Boolean): INodeClass? {
        return DataHolder.instance.manager?.nodeClassAdd(
                id = id,
                name = name,
                alias = alias,
                connectable = connectable,
                tags = tags.toMutableList().apply { add(DataHolder.instance.domainNodeInfo.nodeId) },
                description = description?.value
        )?.apply {
            embeddedFields.forEach { field ->
                if (field.autoAddNew())
                    field.addNew()
            }
        }
    }

    override fun update(name: String, alias: String, description: Any?) {
        DataHolder.instance.manager?.nodeClassUpdate(
                id = id,
                name = name,
                alias = alias,
                description = description
        )
    }

    override fun doDelete() {
        DataHolder.instance.manager?.nodeClassDelete(id)
    }
}

abstract class IEntityField<T>(id: String) : IBaseStructure<IField>(id) {
    override fun dataType(): String = "EntityField"
    override fun parentId(): String? = null
    override fun nodeClassId(): String? = source?.nodeClassId
    abstract fun createFieldValue(entityId: String): EntityFieldValue<T>

    companion object {
        fun addNew(key: String, nodeClassId: String, name: String, alias: String, tags: List<String>, through: Boolean, description: Any?): IField? =
                DataHolder.instance.manager?.fieldAdd(
                        key = key,
                        nodeClassId = nodeClassId,
                        name = name,
                        alias = alias,
                        through = through,
                        tags = tags,
                        description = description
                )
    }

    fun getFieldValue(entity: IEntity<*>): EntityFieldValue<T> {
        val fieldValueId = compose(entity.id, id)
        return cast(DataHolder.instance.entityFieldValueHolder.getOrPut(fieldValueId) {
            val entityFieldValue = createFieldValue(entity.id)
            entityFieldValue.source = DataHolder.instance.bootstrap.fieldValueService.getById(fieldValueId)
            entityFieldValue
        })!!
    }

    override fun update(name: String, alias: String, description: Any?) {
        DataHolder.instance.manager?.fieldUpdate(
                id = id,
                name = name,
                alias = alias,
                description = description
        )
    }

    override fun doDelete() {
        DataHolder.instance.manager?.fieldDelete(id)
    }


}

abstract class IEmbeddedEntityField<T>(private val nodeClassId: String, private val key: String) : IEntityField<T>(compose(nodeClassId, key)) {
    override fun dataName(): String = "EmbeddedField"
    open fun defaultValue(): T? = null
    open fun autoAddNew(): Boolean = false
    open fun autoInitValue(): Boolean = false
    open fun addNew() {}
    fun addNew(name: String, alias: String, tags: List<String>, through: Boolean, description: Any?): IField? {
        return addNew(key, nodeClassId, name, alias, tags, through, description)
    }

    fun setDefaultValueToEntity(entityId: String) =
            defaultValue()?.apply {
                createFieldValue(entityId).update(this as Any)
            }
}

abstract class IEntity<T : IEntityClass>(id: String, val entityClass: T) : IBaseStructure<INode>(id) {
    override fun dataType(): String = "Entity"
    override fun parentId(): String? = source?.path?.lastOrNull()
    override fun nodeClassId(): String? = source?.nodeClassId
    override fun path(): List<String> = source?.path ?: emptyList()


    protected fun addNew(parentId: String,
                         name: String,
                         alias: String,
                         tags: List<String>,
                         externalNodeIdScope: List<String>,
                         externalNodeClassTagScope: List<String>): INode? {
        return DataHolder.instance.manager?.nodeAdd(
                id = id,
                nodeClassId = entityClass.id,
                name = name,
                alias = alias,
                parentId = parentId,
                externalNodeIdScope = externalNodeIdScope,
                externalNodeClassTagScope = externalNodeClassTagScope,
                tags = tags,
                description = description?.value
        )?.apply {
            entityClass.embeddedFields.forEach { field ->
                if (field.autoInitValue())
                    field.setDefaultValueToEntity(this.id)
            }
        }
    }

    fun getCustomFieldValue(fieldId: String): EntityFieldValue<ValueDescription>? =
            cast<CustomField>(DataHolder.instance.entityFieldHolder[fieldId])?.getFieldValue(this)

    override fun update(name: String, alias: String, description: Any?) {
        DataHolder.instance.manager?.nodeUpdate(
                id = id,
                name = name,
                alias = alias,
                description = description
        )
    }

    override fun doDelete() {
        DataHolder.instance.manager?.nodeDelete(id)
    }
}

abstract class IBaseProperty<T, U : IBaseData>(private val castFunction: (raw: U) -> T, initValue: T? = null) : ObservableOnSubscribe<T> {
    private var emitters = mutableListOf<ObservableEmitter<T>>()
    private var lastParseTime: Long = -1
    protected var v: T? = initValue

    val value: T?
        get() {
            if (source?.updateTime ?: -1 > lastParseTime) {
                parseValue(source!!)
            }
            return v
        }
    val observable: Observable<T>
        get() = Observable.create(this).doFinally {
            emitters = emitters.filter { !it.isDisposed }.toMutableList()
        }
    var source: U? = null
        set(value) {
            field = value
            if (value != null) {
                sourceUpdated(value)
                if (emitters.isNotEmpty()) {
                    if (parseValue(value)) {
                        publish(v!!)
                    }
                }
            }
        }

    protected open fun sourceUpdated(s: U) {

    }

    protected open fun deleteOldDependency() {

    }

    protected open fun addNewDependency() {

    }

    private fun parseValue(u: U): Boolean {
        lastParseTime = u.updateTime
        val casted = castFunction(u)
        return if (v != casted) {
            v = casted
            true
        } else
            false
    }

    protected fun publish(t: T) {
        emitters.forEach {
            if (!it.isDisposed)
                it.onNext(t)
        }
    }

    override fun subscribe(emitter: ObservableEmitter<T>) {
        value?.apply {
            emitter.onNext(this)
        }
        emitters.add(emitter)
    }

    open fun delete() {
        emitters.forEach {
            if (!it.isDisposed)
                it.onComplete()
        }
        emitters.clear()
    }
}

open class EntityFieldValue<T>(private val nodeId: String, private val fieldId: String, castFunction: (raw: IFieldValue) -> T) : IBaseProperty<T, IFieldValue>(castFunction) {
    val session: String
        get() = source?.session ?: ""
    val updateTime: Long
        get() = source?.updateTime ?: -1

    fun update(v: Any, session: String? = null) =
            DataHolder.instance.bootstrap.fieldValueService.setFieldValue(
                    nodeId,
                    fieldId,
                    if (v is String)
                        v
                    else
                        stringfy(v),
                    session)

}

interface IDependOnOthers {
    fun id(): String
    fun dependence(): List<String>
}

data class StructureDataUpdateCapsule(
        var id: String,
        var dataName: String,
        var dataType: String,
        var updateType: String,
        var content: String,
        var updateTime: Long
)