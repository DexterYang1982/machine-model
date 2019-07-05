package net.gridtech.machine.model

import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.disposables.Disposable
import net.gridtech.core.data.*
import net.gridtech.core.util.APIExceptionEnum
import net.gridtech.core.util.generateId

abstract class IData<T : IBaseData>(initData: T, observable: Observable<Triple<ChangedType, String, T?>>) {
    lateinit var data: T
    val updatePublisher = Observable.concat(
            Observable.just(initData),
            observable.filter { it.first == ChangedType.UPDATE && it.second == initData.id }.map { it.third!! }
    ).publish()

    val deletePublisher = observable.filter { it.first == ChangedType.DELETE && it.second == initData.id }.publish()
    private var updateDisposable: Disposable? = null
    private var deleteDisposable: Disposable? = null

    fun start() {
        updatePublisher.subscribe {
            data = it
            println("====${javaClass.simpleName} Updated (${data.id}) ====")
        }
        deletePublisher.subscribe {
            updateDisposable?.dispose()
            deleteDisposable?.dispose()
            System.err.println("====${javaClass.simpleName} Removed (${data.id}) ====")
        }
        updateDisposable = updatePublisher.connect()
        deleteDisposable = deletePublisher.connect()
    }

    fun onDelete(cleanUpFunction: (data: T) -> Unit) {
        deletePublisher.doOnNext { cleanUpFunction(data) }
    }
}

abstract class IEntityClass(initData: INodeClass) : IData<INodeClass>(initData, DataHolder.instance.nodeClassPublisher) {
    open fun getDescription(): Any? = null

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
                id = data.id,
                name = name,
                alias = alias,
                description = description
        )
    }

    fun updateNameAndAlias(name: String, alias: String) {
        update(name, alias, getDescription())
    }

    fun updateDescription(description: Any?) {
        update(data.name, data.alias, description)
    }

    fun delete() {
        DataHolder.instance.manager?.nodeClassDelete(data.id)
    }

}

abstract class IEntityField(initData: IField) : IData<IField>(initData, DataHolder.instance.fieldPublisher) {
    open fun getDescription(): Any? = null

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
                id = data.id,
                name = name,
                alias = alias,
                description = description
        )
    }

    fun updateNameAndAlias(name: String, alias: String) {
        update(name, alias, getDescription())
    }

    fun updateDescription(description: Any?) {
        update(data.name, data.alias, description)
    }

    fun delete() {
        APIExceptionEnum.ERR10_CAN_NOT_BE_DELETED.assert(DataHolder.instance.checkDependency(data.id))
        DataHolder.instance.manager?.fieldDelete(data.id)
    }
}

abstract class IEntity(initData: INode) : IData<INode>(initData, DataHolder.instance.nodePublisher) {
    open fun getDescription(): Any? = null

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
                id = data.id,
                name = name,
                alias = alias,
                description = description
        )
    }

    fun updateNameAndAlias(name: String, alias: String) {
        update(name, alias, getDescription())
    }

    fun updateDescription(description: Any?) {
        update(data.name, data.alias, description)
    }

    fun delete() {
        APIExceptionEnum.ERR10_CAN_NOT_BE_DELETED.assert(DataHolder.instance.checkDependency(data.id))
        DataHolder.instance.manager?.nodeDelete(data.id)
    }
}

abstract class IEntityFieldValue(initData: IFieldValue) : IData<IFieldValue>(initData, DataHolder.instance.fieldValuePublisher) {
}

abstract class IBaseProperty<T, U : IBaseData>(val castFunction: (raw: U) -> T, initValue: T? = null) : ObservableOnSubscribe<T> {
    private var emitters = mutableListOf<ObservableEmitter<T>>()
    var value: T? = initValue
    val observable: Observable<T>
        get() = Observable.create(this).doFinally {
            emitters = emitters.filter { !it.isDisposed }.toMutableList()
        }
    var source: U? = null
        set(value) {
            field = value
            field?.apply {
                sourceUpdated(this)
                parseValue(this)
            }
        }

    protected open fun sourceUpdated(s: U) {

    }

    private fun parseValue(u: U) {
        val casted = castFunction(u)
        if (value != casted) {
            value = casted
            publish(casted)
        }
    }

    private fun publish(c: T) {
        if (emitters.isNotEmpty()) {
            emitters.forEach {
                if (!it.isDisposed)
                    it.onNext(c)
            }
        }
    }

    override fun subscribe(emitter: ObservableEmitter<T>) {
        value?.apply {
            emitter.onNext(this)
        }
        emitters.add(emitter)
    }

    fun delete() {
        emitters.forEach {
            if (!it.isDisposed)
                it.onComplete()
        }
        emitters.clear()
    }
}

abstract class IProperty<T, U : IBaseData>(
        updatePublisher: Observable<U>,
        deletePublisher: Observable<*>,
        castFunction: (raw: U) -> T) {
    lateinit var data: U
    var current: T? = null
    open fun deleteOldDependency() {}
    open fun addNewDependency() {}
    private val dataChangedPublisher = updatePublisher
            .map {
                data = it
                val update = castFunction(data)
                if (current != update) {
                    deleteOldDependency()
                    current = update
                    addNewDependency()
                    true
                } else false
            }
            .filter { it }
            .map { current!! }
            .publish()

    init {
        val dataChangedDisposable = dataChangedPublisher.connect()
        deletePublisher.subscribe { dataChangedDisposable.dispose() }
    }

    fun watch() = Observable.concat(
            current?.let { Observable.just(it) } ?: Observable.empty<T>(),
            dataChangedPublisher
    )
}

interface IDependOnOthers {
    fun id(): String
    fun dependence(): List<String>
}