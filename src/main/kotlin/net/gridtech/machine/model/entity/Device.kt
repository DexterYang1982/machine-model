package net.gridtech.machine.model.entity

import net.gridtech.core.data.INode
import net.gridtech.machine.model.IBaseProperty
import net.gridtech.machine.model.IEntity
import net.gridtech.machine.model.entityClass.DeviceClass
import net.gridtech.machine.model.property.entity.DeviceDefinitionDescription


class Device : IEntity<DeviceClass> {
    constructor(node: INode) : super(node)
    constructor(id: String, t: DeviceClass) : super(id, t)

    fun addNew(parentId: String, name: String, alias: String) =
            addNew(
                    parentId,
                    name,
                    alias,
                    tags,
                    emptyList(),
                    emptyList()
            )

    val description = DeviceDefinitionDescription(this)
    override fun getDescriptionProperty(): IBaseProperty<*, INode>? = description


    companion object {
        val tags = listOf("device")
        fun create(node: INode): Device? =
                if (node.tags.containsAll(tags))
                    Device(node)
                else
                    null
    }
}