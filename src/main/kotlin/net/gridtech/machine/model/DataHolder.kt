package net.gridtech.machine.model

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import net.gridtech.core.Bootstrap
import net.gridtech.core.data.*
import net.gridtech.core.util.cast
import net.gridtech.machine.model.entity.*
import net.gridtech.machine.model.entityClass.*
import net.gridtech.machine.model.entityField.CustomField

class DataHolder(val bootstrap: Bootstrap, val manager: IManager? = null) {
    lateinit var domainNodeInfo: IHostInfo
    val entityClassHolder = HashMap<String, IEntityClass>()
    val entityFieldHolder = HashMap<String, IEntityField<*>>()
    val entityHolder = HashMap<String, IEntity<*>>()
    val entityFieldValueHolder = HashMap<String, EntityFieldValue<*>>()
    val structureDataChangedPublisher = PublishSubject.create<Pair<StructureDataChangedType, IBaseStructure<*>>>()
    private val dependencyMap = HashMap<String, List<String>>()

    companion object {
        lateinit var instance: DataHolder
    }

    private fun watchNodeClass() {
        bootstrap.dataPublisher<INodeClass>(bootstrap.nodeClassService.serviceName)
                .subscribe {
                    when (it.first) {
                        ChangedType.UPDATE -> {
                            val nodeClass = it.third!!
                            if (entityClassHolder.containsKey(it.second)) {
                                val entityClass = entityClassHolder[it.second]!!
                                entityClass.source = nodeClass
                                structureDataChangedPublisher.onNext(StructureDataChangedType.UPDATE to entityClass)
                            } else {
                                createEntityClass(nodeClass)?.apply {
                                    entityClassHolder[nodeClass.id] = this
                                    structureDataChangedPublisher.onNext(StructureDataChangedType.ADD to this)
                                }
                            }
                        }
                        ChangedType.DELETE -> {
                            entityClassHolder.remove(it.second)?.apply {
                                delete()
                                structureDataChangedPublisher.onNext(StructureDataChangedType.DELETE to this)
                            }
                        }
                        ChangedType.FINISHED -> watchField()
                    }
                }
    }

    private fun watchField() {
        bootstrap.dataPublisher<IField>(bootstrap.fieldService.serviceName)
                .subscribe {
                    when (it.first) {
                        ChangedType.UPDATE -> {
                            val field = it.third!!
                            if (entityFieldHolder.containsKey(it.second)) {
                                val entityField = entityFieldHolder[field.id]!!
                                entityField.source = field
                                structureDataChangedPublisher.onNext(StructureDataChangedType.UPDATE to entityField)
                            } else {
                                CustomField.create(field)?.apply {
                                    entityFieldHolder[field.id] = this
                                    structureDataChangedPublisher.onNext(StructureDataChangedType.ADD to this)
                                }
                            }
                        }
                        ChangedType.DELETE -> {
                            entityFieldHolder.remove(it.second)?.apply {
                                delete()
                                structureDataChangedPublisher.onNext(StructureDataChangedType.DELETE to this)
                            }
                        }
                        ChangedType.FINISHED -> watchNode()
                    }
                }
    }

    private fun watchNode() {
        bootstrap.dataPublisher<INode>(bootstrap.nodeService.serviceName)
                .subscribe {
                    when (it.first) {
                        ChangedType.UPDATE -> {
                            val node = it.third!!
                            if (entityHolder.containsKey(it.second)) {
                                val entity = entityHolder[it.second]!!
                                entity.source = node
                                structureDataChangedPublisher.onNext(StructureDataChangedType.UPDATE to entity)
                            } else {
                                createEntity(node)?.apply {
                                    entityHolder[node.id] = this
                                    structureDataChangedPublisher.onNext(StructureDataChangedType.ADD to this)
                                }
                            }
                        }
                        ChangedType.DELETE -> {
                            entityHolder.remove(it.second)?.apply {
                                delete()
                                structureDataChangedPublisher.onNext(StructureDataChangedType.DELETE to this)
                            }
                        }
                        ChangedType.FINISHED -> watchFieldValue()
                    }
                }
    }

    private fun watchFieldValue() {
        bootstrap.dataPublisher<IFieldValue>(bootstrap.fieldValueService.serviceName)
                .subscribe {
                    when (it.first) {
                        ChangedType.UPDATE -> {
                            entityFieldValueHolder[it.second]?.source = it.third
                        }
                        ChangedType.DELETE -> {
                            entityFieldValueHolder.remove(it.second)?.delete()
                        }
                        ChangedType.FINISHED -> {
                        }
                    }
                }
    }

    init {
        instance = this
        watchNodeClass()
    }

    fun addDependency(dependOnOthers: IDependOnOthers) {
        dependencyMap[dependOnOthers.id()] = dependOnOthers.dependence()
    }

    fun deleteDependency(id: String) = dependencyMap.remove(id)

    fun checkDependency(id: String) =
            dependencyMap.values.find { it.contains(id) } == null

    fun <T : IEntity<*>> getEntityByTagsObservable(tags: List<String>): Observable<T> =
            Observable.concat(
                    Observable.fromIterable(entityHolder.values.mapNotNull { entity ->
                        if (entity.source?.tags?.containsAll(tags) == true)
                            cast<T>(entity)!!
                        else
                            null
                    }),
                    structureDataChangedPublisher.filter {
                        it.first == StructureDataChangedType.ADD && it.second is IEntity<*> && it.second.source?.tags?.containsAll(tags) == true
                    }.map { cast<T>(it)!! }
            )

    fun getEntityByConditionObservable(condition: (entity: IEntity<*>) -> Boolean): Observable<IEntity<*>> =
            Observable.concat(
                    Observable.fromIterable(entityHolder.values.filter { entity -> condition(entity) }),
                    structureDataChangedPublisher.filter { (type, structure) ->
                        type == StructureDataChangedType.ADD && structure is IEntity<*> && condition(structure)
                    }.map { it.second as IEntity<*> }
            )

    fun getEntityFieldByConditionObservable(condition: (entity: IEntityField<*>) -> Boolean): Observable<IEntityField<*>> =
            Observable.concat(
                    Observable.fromIterable(entityFieldHolder.values.filter { entityField -> condition(entityField) }),
                    structureDataChangedPublisher.filter { (type, structure) ->
                        type == StructureDataChangedType.ADD && structure is IEntityField<*> && condition(structure)
                    }.map { it.second as IEntityField<*> }
            )

    fun <T : IEntity<*>> getEntityByIdObservable(id: String): Single<T> =
            entityHolder[id]
                    ?.let { entity ->
                        Single.just(cast<T>(entity)!!)
                    }
                    ?: structureDataChangedPublisher.filter {
                        it.first == StructureDataChangedType.ADD && it.second is IEntity<*> && it.second.id == id
                    }.map { cast<T>(it)!! }.singleOrError()

    private fun createEntityClass(nodeClass: INodeClass): IEntityClass? = null
            ?: RootClass.create(nodeClass)
            ?: DomainClass.create(nodeClass)
            ?: MachineClass.create(nodeClass)
            ?: ModbusSlaveClass.create(nodeClass)
            ?: ModbusUnitClass.create(nodeClass)
            ?: GroupClass.create(nodeClass)
            ?: DisplayClass.create(nodeClass)
            ?: CabinClass.create(nodeClass)
            ?: DeviceClass.create(nodeClass)
            ?: TunnelClass.create(nodeClass)


    private fun createEntity(node: INode): IEntity<*>? = null
            ?: Root.create(node)
            ?: Domain.create(node)
            ?: Machine.create(node)
            ?: ModbusSlave.create(node)
            ?: ModbusUnit.create(node)
            ?: Group.create(node)
            ?: Display.create(node)
            ?: Cabin.create(node)
            ?: Device.create(node)
            ?: Tunnel.create(node)

}

enum class StructureDataChangedType {
    ADD,
    UPDATE,
    DELETE
}