package net.gridtech.machine.model

import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import net.gridtech.core.data.*
import net.gridtech.core.util.APIExceptionEnum
import net.gridtech.core.util.generateId

abstract class IBaseStructure<T : IStructureData>(initData: T) {
    open fun getDescriptionProperty(): IBaseProperty<*, T>? = null
    val nameProperty = object : IBaseProperty<String, IStructureData>({ structure ->
        structure.name
    }) {}
    val aliasProperty = object : IBaseProperty<String, IStructureData>({ structure ->
        structure.alias
    }) {}

    var source: T = initData
        set(value) {
            field = value
            nameProperty.source = value
            aliasProperty.source = value
            getDescriptionProperty()?.source = value

            System.err.println("[Update] ${javaClass.simpleName}  id=${source.id}")
        }


    init {
        System.err.println("[Create]  ${javaClass.simpleName}  id=${source.id}")
    }


    fun onDelete() {
        System.err.println("[Delete] ${javaClass.simpleName}  id=${source.id}")
        nameProperty.onDelete()
        aliasProperty.onDelete()
        getDescriptionProperty()?.onDelete()
    }
}

abstract class IEntityClass(initData: INodeClass) : IBaseStructure<INodeClass>(initData) {
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

    private fun update(name: String, alias: String, description: Any?) {
        DataHolder.instance.manager?.nodeClassUpdate(
                id = source.id,
                name = name,
                alias = alias,
                description = description
        )
    }

    fun updateNameAndAlias(name: String, alias: String) {
        update(name, alias, getDescriptionProperty()?.value)
    }

    fun updateDescription(description: Any?) {
        update(source.name, source.alias, description)
    }

    fun delete() {
        DataHolder.instance.manager?.nodeClassDelete(source.id)
    }

}

abstract class IEntityField(initData: IField) : IBaseStructure<IField>(initData) {
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

    private fun update(name: String, alias: String, description: Any?) {
        DataHolder.instance.manager?.fieldUpdate(
                id = source.id,
                name = name,
                alias = alias,
                description = description
        )
    }

    fun updateNameAndAlias(name: String, alias: String) {
        update(name, alias, getDescriptionProperty()?.value)
    }

    fun updateDescription(description: Any?) {
        update(source.name, source.alias, description)
    }

    fun delete() {
        APIExceptionEnum.ERR10_CAN_NOT_BE_DELETED.assert(DataHolder.instance.checkDependency(source.id))
        DataHolder.instance.manager?.fieldDelete(source.id)
    }
}

abstract class IEntity(initData: INode) : IBaseStructure<INode>(initData) {
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

    private fun update(name: String, alias: String, description: Any?) {
        DataHolder.instance.manager?.nodeUpdate(
                id = source.id,
                name = name,
                alias = alias,
                description = description
        )
    }

    fun updateNameAndAlias(name: String, alias: String) {
        update(name, alias, getDescriptionProperty()?.value)
    }

    fun updateDescription(description: Any?) {
        update(source.name, source.alias, description)
    }

    fun delete() {
        APIExceptionEnum.ERR10_CAN_NOT_BE_DELETED.assert(DataHolder.instance.checkDependency(source.id))
        DataHolder.instance.manager?.nodeDelete(source.id)
    }
}

abstract class IBaseProperty<T, U : IBaseData>(private val castFunction: (raw: U) -> T, initValue: T? = null) : ObservableOnSubscribe<T> {
    private var emitters = mutableListOf<ObservableEmitter<T>>()
    private var lastParseTime: Long = -1
    private var _value: T? = initValue

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

    fun onDelete() {
        emitters.forEach {
            if (!it.isDisposed)
                it.onComplete()
        }
        emitters.clear()
    }
}

interface IDependOnOthers {
    fun id(): String
    fun dependence(): List<String>
}