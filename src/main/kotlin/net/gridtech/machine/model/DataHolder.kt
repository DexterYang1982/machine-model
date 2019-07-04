package net.gridtech.machine.model

import net.gridtech.core.Bootstrap
import net.gridtech.core.data.*

class DataHolder(bootstrap: Bootstrap,val domain:String, val manager: IManager? = null) {
    val nodeClassPublisher = bootstrap.dataPublisher<INodeClass>(bootstrap.nodeClassService.serviceName)
    val fieldPublisher = bootstrap.dataPublisher<IField>(bootstrap.fieldService.serviceName)
    val nodePublisher = bootstrap.dataPublisher<INode>(bootstrap.nodeService.serviceName)
    val fieldValuePublisher = bootstrap.dataPublisher<IFieldValue>(bootstrap.fieldValueService.serviceName)

    val entityClassHolder = HashMap<String, IEntityClass>()
    val entityFieldHolder = HashMap<String, IEntityField>()
    val entityHolder = HashMap<String, IEntity>()
    val entityFieldValueHolder = HashMap<String, IEntityFieldValue>()

    companion object {
        lateinit var instance: DataHolder
    }

    init {
        instance = this
        nodeClassPublisher
                .filter { !entityClassHolder.containsKey(it.second) && it.first == ChangedType.UPDATE }
                .subscribe {
                    val nodeClass = it.third!!
                    if (ModbusUnitClass.match(nodeClass)) {
                        ModbusUnitClass(nodeClass)
                    } else {
                        null
                    }?.apply {
                        entityClassHolder[nodeClass.id] = this
                        this.onDelete { toDelete ->
                            entityClassHolder.remove(toDelete.id)
                        }
                        this.start()
                    }
                }

        nodeClassPublisher.connect()
        fieldPublisher.connect()
        nodePublisher.connect()
        fieldValuePublisher.connect()
    }
}