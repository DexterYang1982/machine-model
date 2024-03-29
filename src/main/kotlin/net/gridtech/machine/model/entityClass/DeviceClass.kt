package net.gridtech.machine.model.entityClass

import net.gridtech.core.data.INodeClass
import net.gridtech.machine.model.IEntityClass
import net.gridtech.machine.model.entityField.DeviceCurrentProcessField
import net.gridtech.machine.model.entityField.DeviceHealthField
import net.gridtech.machine.model.entityField.DeviceProcessQueueField

class DeviceClass(id: String) : IEntityClass(id) {
    val healthy = DeviceHealthField(this)
    val processQueue = DeviceProcessQueueField(this)
    val currentProcess = DeviceCurrentProcessField(this)

    fun addNew(name: String, alias: String) =
            addNew(name, alias, tags, false)

    companion object {
        val tags = listOf("device class")
        fun create(nodeClass: INodeClass): DeviceClass? =
                if (nodeClass.tags.containsAll(tags))
                    DeviceClass(nodeClass.id).apply { initialize(nodeClass) }
                else
                    null
    }
}