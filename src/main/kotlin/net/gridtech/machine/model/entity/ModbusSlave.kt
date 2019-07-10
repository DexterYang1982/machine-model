package net.gridtech.machine.model.entity

import net.gridtech.core.data.INode
import net.gridtech.core.util.generateId
import net.gridtech.machine.model.IBaseProperty
import net.gridtech.machine.model.IEntity
import net.gridtech.machine.model.entityClass.ModbusSlaveClass
import net.gridtech.machine.model.property.entity.ModbusSlaveDefinitionDescription
import net.gridtech.machine.model.property.entity.SlaveAddress


class ModbusSlave(node: INode) : IEntity<ModbusSlaveClass>(node) {
    val description = ModbusSlaveDefinitionDescription(this)
    override fun getDescriptionProperty(): IBaseProperty<*, INode>? = description

    companion object {
        val tags = listOf("modbus slave")
        fun create(node: INode): ModbusSlave? =
                if (node.tags.containsAll(tags))
                    ModbusSlave(node)
                else
                    null

        fun add(modbusSlaveClass: ModbusSlaveClass, parentId: String, name: String, alias: String) =
                add(
                        id = generateId(),
                        parentId = parentId,
                        nodeClassId = modbusSlaveClass.id,
                        name = name,
                        alias = alias,
                        tags = tags,
                        externalNodeIdScope = emptyList(),
                        externalNodeClassTagScope = emptyList(),
                        description = SlaveAddress.empty()
                )?.apply {
                }
    }
}