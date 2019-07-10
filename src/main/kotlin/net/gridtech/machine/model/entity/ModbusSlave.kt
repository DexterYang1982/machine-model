package net.gridtech.machine.model.entity

import net.gridtech.core.data.INode
import net.gridtech.machine.model.IBaseProperty
import net.gridtech.machine.model.IEntity
import net.gridtech.machine.model.entityClass.ModbusSlaveClass
import net.gridtech.machine.model.property.entity.ModbusSlaveDefinitionDescription


class ModbusSlave : IEntity<ModbusSlaveClass> {
    constructor(node: INode) : super(node)
    constructor(id: String, t: ModbusSlaveClass) : super(id, t)

    fun addNew(parentId: String, name: String, alias: String) =
            addNew(
                    parentId,
                    name,
                    alias,
                    tags,
                    emptyList(),
                    emptyList()
            )

    val description = ModbusSlaveDefinitionDescription(this)
    override fun getDescriptionProperty(): IBaseProperty<*, INode>? = description

    companion object {
        val tags = listOf("modbus slave")
        fun create(node: INode): ModbusSlave? =
                if (node.tags.containsAll(tags))
                    ModbusSlave(node)
                else
                    null
    }
}