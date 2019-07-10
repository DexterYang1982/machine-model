package net.gridtech.machine.model.entityClass

import net.gridtech.core.data.INodeClass
import net.gridtech.machine.model.IEntityClass
import net.gridtech.machine.model.entityField.DeviceHealthField

class DeviceClass(id: String) : IEntityClass(id) {
    val healthy = DeviceHealthField(this)

    fun addNew(name: String, alias: String) {
        addNew(name, alias, tags, false)
    }

    companion object {
        val tags = listOf("device class")
        fun create(nodeClass: INodeClass): DeviceClass? =
                if (nodeClass.tags.containsAll(tags))
                    DeviceClass(nodeClass.id).apply { initialize(nodeClass) }
                else
                    null
    }
}