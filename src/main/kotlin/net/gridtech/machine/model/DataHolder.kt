package net.gridtech.machine.model

import io.reactivex.observables.ConnectableObservable
import net.gridtech.core.Bootstrap
import net.gridtech.core.data.*
import net.gridtech.machine.model.entity.Machine
import net.gridtech.machine.model.entityClass.MachineClass
import net.gridtech.machine.model.entityClass.ModbusUnitClass
import net.gridtech.machine.model.entityField.CustomField

class DataHolder(bootstrap: Bootstrap, val domainNodeId: String, val manager: IManager? = null) {
    val nodeClassPublisher = bootstrap.dataPublisher<INodeClass>(bootstrap.nodeClassService.serviceName)
    val fieldPublisher = bootstrap.dataPublisher<IField>(bootstrap.fieldService.serviceName)
    val nodePublisher = bootstrap.dataPublisher<INode>(bootstrap.nodeService.serviceName)
    val fieldValuePublisher = bootstrap.dataPublisher<IFieldValue>(bootstrap.fieldValueService.serviceName)

    val entityClassHolder = HashMap<String, IEntityClass>()
    val entityFieldHolder = HashMap<String, IEntityField>()
    val entityHolder = HashMap<String, IEntity>()
    val entityFieldValueHolder = HashMap<String, IEntityFieldValue>()

    val entityClassAddedPublisher: ConnectableObservable<out IEntityClass> =
            nodeClassPublisher
                    .filter { !entityClassHolder.containsKey(it.second) && it.first == ChangedType.UPDATE }
                    .map {
                        val nodeClass = it.third!!
                        val entityClassAdded = createEntityClass(nodeClass)
                        entityClassAdded?.apply {
                            entityClassHolder[nodeClass.id] = this
                            this.onDelete { toDelete ->
                                entityClassHolder.remove(toDelete.id)
                            }
                            this.start()
                        }
                        (entityClassAdded to (entityClassAdded != null))
                    }
                    .filter { it.second }
                    .map { it.first!! }
                    .publish()
    val entityFieldAddedPublisher: ConnectableObservable<out IEntityField> =
            fieldPublisher
                    .filter { !entityFieldHolder.containsKey(it.second) && it.first == ChangedType.UPDATE }
                    .map {
                        val field = it.third!!
                        val entityFieldAdded = createEntityField(field)
                        entityFieldAdded?.apply {
                            entityFieldHolder[field.id] = this
                            this.onDelete { toDelete ->
                                entityFieldHolder.remove(toDelete.id)
                            }
                            this.start()
                        }
                        (entityFieldAdded to (entityFieldAdded != null))
                    }
                    .filter { it.second }
                    .map { it.first!! }
                    .publish()

    val entityAddedPublisher: ConnectableObservable<out IEntity> =
            nodePublisher
                    .filter { !entityHolder.containsKey(it.second) && it.first == ChangedType.UPDATE }
                    .map {
                        val node = it.third!!
                        val entityAdded = createEntity(node)
                        entityAdded?.apply {
                            entityHolder[node.id] = this
                            this.onDelete { toDelete ->
                                entityHolder.remove(toDelete.id)
                            }
                            this.start()
                        }
                        (entityAdded to (entityAdded != null))
                    }
                    .filter { it.second }
                    .map { it.first!! }
                    .publish()


    private val dependencyMap = HashMap<String, List<String>>()

    companion object {
        lateinit var instance: DataHolder
    }

    init {
        instance = this

        entityClassAddedPublisher.connect()
        entityFieldAddedPublisher.connect()
        entityAddedPublisher.connect()

        nodeClassPublisher.connect()
        fieldPublisher.connect()
        nodePublisher.connect()
        fieldValuePublisher.connect()
    }

    fun addDependency(dependOnOthers: IDependOnOthers) {
        dependencyMap[dependOnOthers.id()] = dependOnOthers.dependence()
    }

    fun deleteDependency(id: String) = dependencyMap.remove(id)

    fun checkDependency(id: String) =
            dependencyMap.values.find { it.contains(id) } == null


    private fun createEntityClass(nodeClass: INodeClass): IEntityClass? =
            MachineClass.create(nodeClass)
                    ?: ModbusUnitClass.create(nodeClass)

    private fun createEntityField(field: IField): IEntityField? =
            CustomField.create(field)


    private fun createEntity(node: INode): IEntity? =
            Machine.create(node)

}
