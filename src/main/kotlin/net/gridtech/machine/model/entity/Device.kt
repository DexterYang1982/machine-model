package net.gridtech.machine.model.entity

import net.gridtech.core.data.INode
import net.gridtech.core.util.cast
import net.gridtech.machine.model.DataHolder
import net.gridtech.machine.model.IEntity
import net.gridtech.machine.model.entityClass.DeviceClass
import net.gridtech.machine.model.property.entity.DeviceDefinitionDescription


class Device(id: String, entityClass: DeviceClass) : IEntity<DeviceClass>(id, entityClass) {
    override val description = DeviceDefinitionDescription(this)
    fun addNew(parentId: String, name: String, alias: String) =
            addNew(
                    parentId,
                    name,
                    alias,
                    tags,
                    emptyList(),
                    emptyList()
            )

    companion object {
        val tags = listOf("device")
        fun create(node: INode): Device? =
                if (node.tags.containsAll(tags))
                    Device(node.id, cast(DataHolder.instance.entityClassHolder[node.nodeClassId])!!).apply { initialize(node) }
                else
                    null
    }
}