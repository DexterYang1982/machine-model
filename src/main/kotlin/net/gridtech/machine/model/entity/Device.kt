package net.gridtech.machine.model.entity

import net.gridtech.core.data.INode
import net.gridtech.core.util.generateId
import net.gridtech.machine.model.IBaseProperty
import net.gridtech.machine.model.IEntity
import net.gridtech.machine.model.entityClass.DeviceClass
import net.gridtech.machine.model.property.entity.DeviceDefinitionDescription


class Device(node: INode) : IEntity(node) {
    val deviceDefinition = DeviceDefinitionDescription(this)
    override fun getDescriptionProperty(): IBaseProperty<*, INode>? = deviceDefinition

    companion object {
        val tags = listOf("device")
        fun create(node: INode): Device? =
                if (node.tags.containsAll(tags))
                    Device(node)
                else
                    null

        fun add(deviceClass: DeviceClass, parentId: String, name: String, alias: String) =
                add(
                        id = generateId(),
                        parentId = parentId,
                        nodeClassId = deviceClass.source.id,
                        name = name,
                        alias = alias,
                        tags = tags,
                        externalNodeIdScope = emptyList(),
                        externalNodeClassTagScope = emptyList(),
                        description = null
                )?.apply {
                }
    }
}