package net.gridtech.machine.model

import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
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

    open fun getDescriptionProperty(): IBaseProperty<*, T>? = null

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
                getDescriptionProperty()?.source = v
            }
        }

    abstract fun update(name: String, alias: String, description: Any?)

    fun updateNameAndAlias(name: String, alias: String) =
            source?.apply {
                update(name, alias, getDescriptionProperty()?.value)
            }

    fun updateDescription(description: Any?) =
            source?.apply {
                update(this.name, this.alias, description)
            }

    fun delete() {
        name.delete()
        alias.delete()
        getDescriptionProperty()?.delete()
        deletePublisher.onNext(this)
    }

    fun onDelete(): Single<*> = deletePublisher.filter { it.id == id }.firstOrError()

    abstract fun doDelete()

    fun tryToDelete() {
        source?.apply {
            APIExceptionEnum.ERR10_CAN_NOT_BE_DELETED.assert(DataHolder.instance.checkDependency(id))
            doDelete()
        }
    }

    companion object {
        val deletePublisher = PublishSubject.create<IBaseStructure<*>>()
    }
}

abstract class IEntityClass(id: String) : IBaseStructure<INodeClass>(id) {
    val embeddedFields = ArrayList<IEmbeddedEntityField<*>>()
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
                tags = tags,
                description = getDescriptionProperty()?.value
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
    abstract fun createFieldValue(entityId: String): EntityFieldValue<T>

    companion object {
        fun addNew(key: String, nodeClassId: String, name: String, alias: String, tags: List<String>, through: Boolean, description: Any?): IField? {
            return DataHolder.instance.manager?.fieldAdd(
                    key = key,
                    nodeClassId = nodeClassId,
                    name = name,
                    alias = alias,
                    through = through,
                    tags = tags,
                    description = description
            )
        }
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
    open fun defaultValue(): T? = null
    open fun autoAddNew(): Boolean = false
    open fun autoInitValue(): Boolean = false
    open fun addNew() {}
    fun addNew(name: String, alias: String, tags: List<String>, through: Boolean, description: Any?): IField? {
        return addNew(key, nodeClassId, name, alias, tags, through, description)
    }

    fun setDefaultValueToEntity(entityId: String) =
            defaultValue()?.apply {
                createFieldValue(entityId).update(this)
            }
}

abstract class IEntity<T : IEntityClass> : IBaseStructure<INode> {
    val entityClass: T

    constructor(node: INode) : super(node.id) {
        entityClass = cast(DataHolder.instance.entityClassHolder[node.nodeClassId])!!
        super.initialize(node)
    }

    constructor(id: String, t: T) : super(id) {
        entityClass = t
    }

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
                description = getDescriptionProperty()?.value
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
    protected var _value: T? = initValue

    val value: T?
        get() {
            if (source?.updateTime ?: -1 > lastParseTime) {
                parseValue(source!!)
            }
            return _value
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
                        publish(_value!!)
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
        return if (_value != casted) {
            _value = casted
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