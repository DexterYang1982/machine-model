package net.gridtech.machine.model

import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import net.gridtech.core.data.*
import net.gridtech.core.util.*
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

    fun onDelete() {
        name.onDelete()
        alias.onDelete()
        getDescriptionProperty()?.onDelete()
    }

    abstract fun doDelete()

    fun delete() {
        source?.apply {
            APIExceptionEnum.ERR10_CAN_NOT_BE_DELETED.assert(DataHolder.instance.checkDependency(id))
            doDelete()
        }
    }
}

abstract class IEntityClass(id: String) : IBaseStructure<INodeClass>(id) {
    private val embeddedFields = ArrayList<IEmbeddedEntityField<*>>()
    override fun initialize(initData: INodeClass?) {
        super.initialize(initData)
        javaClass.methods.filter { method ->
            method.name.startsWith("get") && method.returnType == IEmbeddedEntityField::class.java
        }.forEach { method ->
            embeddedFields.add(method.invoke(this) as IEmbeddedEntityField<*>)
        }
        embeddedFields.forEach { field ->
            DataHolder.instance.entityFieldHolder[field.id] = field
        }
    }

    companion object {
        fun add(name: String, alias: String, connectable: Boolean, tags: List<String>, description: Any?): INodeClass? {
            val nodeClassTags = tags.toMutableList()
            nodeClassTags.add(DataHolder.instance.domainNodeId ?: "")
            return DataHolder.instance.manager?.nodeClassAdd(
                    id = generateId(),
                    name = name,
                    alias = alias,
                    connectable = connectable,
                    tags = nodeClassTags,
                    description = description
            )
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
    companion object {
        fun add(key: String, nodeClassId: String, name: String, alias: String, tags: List<String>, through: Boolean, description: Any?): IField? {
            val fieldTags = tags.toMutableList()
            fieldTags.add(DataHolder.instance.domainNodeId ?: "")
            return DataHolder.instance.manager?.fieldAdd(
                    key = key,
                    nodeClassId = nodeClassId,
                    name = name,
                    alias = alias,
                    through = through,
                    tags = fieldTags,
                    description = description
            )
        }
    }

    abstract fun createFieldValue(entityId: String): EntityFieldValue<T>

    fun getFieldValue(entityId: String): EntityFieldValue<T> {
        val fieldValueId = compose(entityId, id)
        return cast(DataHolder.instance.entityFieldValueHolder.getOrPut(fieldValueId) {
            val entityFieldValue = createFieldValue(entityId)
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

abstract class IEmbeddedEntityField<T>(id: String) : IEntityField<T>(id)

abstract class IEntity<T>(node: INode) : IBaseStructure<INode>(node.id) {
    val entityClass: T = cast(DataHolder.instance.entityClassHolder[node.nodeClassId])!!
    init {
        super.initialize(node)
    }

    companion object {
        fun add(id: String, parentId: String, nodeClassId: String, name: String, alias: String, tags: List<String>,
                externalNodeIdScope: List<String>,
                externalNodeClassTagScope: List<String>,
                description: Any?): INode? {
            val nodeTags = tags.toMutableList()
            nodeTags.add(DataHolder.instance.domainNodeId ?: "")
            return DataHolder.instance.manager?.nodeAdd(
                    id = id,
                    nodeClassId = nodeClassId,
                    name = name,
                    alias = alias,
                    parentId = parentId,
                    externalNodeIdScope = externalNodeIdScope,
                    externalNodeClassTagScope = externalNodeClassTagScope,
                    tags = nodeTags,
                    description = description
            )
        }
    }

    fun getCustomFieldValue(fieldId: String): EntityFieldValue<ValueDescription>? =
            cast<CustomField>(DataHolder.instance.entityFieldHolder[fieldId])?.getFieldValue(this.source.id)


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

    open fun onDelete() {
        emitters.forEach {
            if (!it.isDisposed)
                it.onComplete()
        }
        emitters.clear()
    }
}

open class EntityFieldValue<T>(private val nodeId: String, private val fieldId: String, castFunction: (raw: IFieldValue) -> T) : IBaseProperty<T, IFieldValue>(castFunction) {
    val session: String?
        get() = source?.session
    val updateTime: Long?
        get() = source?.updateTime

    fun update(v: T, session: String? = null) =
            DataHolder.instance.bootstrap.fieldValueService.setFieldValue(
                    nodeId,
                    fieldId,
                    if (v is String)
                        v
                    else
                        stringfy(v as Any),
                    session)

}

interface IDependOnOthers {
    fun id(): String
    fun dependence(): List<String>
}