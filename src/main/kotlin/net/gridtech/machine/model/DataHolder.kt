package net.gridtech.machine.model

import net.gridtech.core.Bootstrap
import net.gridtech.core.data.*
import net.gridtech.machine.model.entity.Domain
import net.gridtech.machine.model.entity.Machine
import net.gridtech.machine.model.entity.Root
import net.gridtech.machine.model.entityClass.DomainClass
import net.gridtech.machine.model.entityClass.MachineClass
import net.gridtech.machine.model.entityClass.ModbusUnitClass
import net.gridtech.machine.model.entityClass.RootClass
import net.gridtech.machine.model.entityField.CustomField
import net.gridtech.machine.model.entityField.RunningStatusField
import net.gridtech.machine.model.entityField.SecretField

class DataHolder(bootstrap: Bootstrap, val domainNodeId: String? = null, val domainNodeClassId: String? = null, val manager: IManager? = null) {
    val entityClassHolder = HashMap<String, IEntityClass>()
    val entityFieldHolder = HashMap<String, IEntityField>()
    val entityHolder = HashMap<String, IEntity>()

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
                            entityClassHolder.remove(it.second)?.onDelete()
                        }
                    }
                }
        bootstrap.dataPublisher<IField>(bootstrap.fieldService.serviceName)
                .subscribe {
                    when (it.first) {
                        ChangedType.UPDATE -> {
                            val field = it.third!!
                            if (entityFieldHolder.containsKey(it.second)) {
                                entityFieldHolder[it.second]!!.source = field
                            } else {
                                createEntityField(field)?.apply {
                                    entityFieldHolder[field.id] = this
                                }
                            }
                        }
                        ChangedType.DELETE -> {
                            entityFieldHolder.remove(it.second)?.onDelete()
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
                                }
                            }
                        }
                        ChangedType.DELETE -> {
                            entityHolder.remove(it.second)?.onDelete()
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


    private fun createEntityClass(nodeClass: INodeClass): IEntityClass? = null
            ?: RootClass.create(nodeClass)
            ?: DomainClass.create(nodeClass)
            ?: MachineClass.create(nodeClass)
            ?: ModbusUnitClass.create(nodeClass)

    private fun createEntityField(field: IField): IEntityField? = null
            ?: CustomField.create(field)
            ?: RunningStatusField.create(field)
            ?: SecretField.create(field)


    private fun createEntity(node: INode): IEntity? = null
            ?: Root.create(node)
            ?: Domain.create(node)
            ?: Machine.create(node)

}
