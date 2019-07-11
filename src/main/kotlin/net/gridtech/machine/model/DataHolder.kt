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
import net.gridtech.machine.model.entityField.TriggerField

class DataHolder(val bootstrap: Bootstrap, val manager: IManager? = null) {
    val entityClassHolder = HashMap<String, IEntityClass>()
    val entityFieldHolder = HashMap<String, IEntityField<*>>()
    val entityHolder = HashMap<String, IEntity<*>>()
    val entityFieldValueHolder = HashMap<String, EntityFieldValue<*>>()

    private val entityAddedPublisher = PublishSubject.create<IEntity<*>>()
    private val triggerAddedPublisher = PublishSubject.create<TriggerField>()


    private val dependencyMap = HashMap<String, List<String>>()

    companion object {
        lateinit var instance: DataHolder
    }

    init {
        instance = this
        bootstrap.dataPublisher<INodeClass>(bootstrap.nodeClassService.serviceName)
                .subscribe {
                    when (it.first) {
                        ChangedType.UPDATE -> {
                            val nodeClass = it.third!!
                            if (entityClassHolder.containsKey(it.second)) {
                                entityClassHolder[it.second]!!.source = nodeClass
                            } else {
                                createEntityClass(nodeClass)?.apply {
                                    entityClassHolder[nodeClass.id] = this
                                }
                            }
                        }
                        ChangedType.DELETE -> {
                            entityClassHolder.remove(it.second)?.delete()
                        }
                    }
                }
        bootstrap.dataPublisher<IField>(bootstrap.fieldService.serviceName)
                .subscribe {
                    when (it.first) {
                        ChangedType.UPDATE -> {
                            val field = it.third!!
                            if (entityFieldHolder.containsKey(it.second)) {
                                entityFieldHolder[field.id]!!.source = field
                            } else {
                                CustomField.create(field)?.apply {
                                    entityFieldHolder[field.id] = this
                                }
                                TriggerField.create(field)?.apply {
                                    entityFieldHolder[field.id] = this
                                    triggerAddedPublisher.onNext(this)
                                }
                            }
                        }
                        ChangedType.DELETE -> {
                            entityFieldHolder.remove(it.second)?.delete()
                        }
                    }
                }
        bootstrap.dataPublisher<INode>(bootstrap.nodeService.serviceName)
                .subscribe {
                    when (it.first) {
                        ChangedType.UPDATE -> {
                            val node = it.third!!
                            if (entityHolder.containsKey(it.second)) {
                                entityHolder[it.second]!!.source = node
                            } else {
                                createEntity(node)?.apply {
                                    entityHolder[node.id] = this
                                    entityAddedPublisher.onNext(this)
                                }
                            }
                        }
                        ChangedType.DELETE -> {
                            entityHolder.remove(it.second)?.delete()
                        }
                    }
                }
        bootstrap.dataPublisher<IFieldValue>(bootstrap.fieldValueService.serviceName)
                .subscribe {
                    when (it.first) {
                        ChangedType.UPDATE -> {
                            entityFieldValueHolder[it.second]?.source = it.third
                        }
                        ChangedType.DELETE -> {
                            entityFieldValueHolder.remove(it.second)?.delete()
                        }
                    }
                }
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
                    entityAddedPublisher.filter { it.source?.tags?.containsAll(tags) == true }.map { cast<T>(it)!! }
            )

    fun <T : IEntity<*>> getEntityByIdObservable(id: String): Single<T> =
            entityHolder[id]
                    ?.let { entity ->
                        Single.just(cast<T>(entity)!!)
                    }
                    ?: entityAddedPublisher
                            .filter { it.source?.id == id }
                            .map { cast<T>(it)!! }.singleOrError()

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
