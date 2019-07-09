package net.gridtech.machine.model.entityClass

import net.gridtech.core.data.INodeClass
import net.gridtech.machine.model.IEntityClass
import net.gridtech.machine.model.entityField.DeviceHealthField

class DeviceClass(nodeClass: INodeClass) : IEntityClass(nodeClass) {
    companion object {
        val tags = listOf("device class")
        fun create(nodeClass: INodeClass): DeviceClass? =
                if (nodeClass.tags.containsAll(tags))
                    DeviceClass(nodeClass)
                else
                    null

        fun add(name: String, alias: String) =
                add(name, alias, false, tags, null)
                        ?.apply {
                            DeviceHealthField.add(this.id)
                        }
    }
}