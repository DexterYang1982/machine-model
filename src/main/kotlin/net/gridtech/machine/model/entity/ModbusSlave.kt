package net.gridtech.machine.model.entity

import net.gridtech.core.data.INode
import net.gridtech.core.util.generateId
import net.gridtech.machine.model.IBaseProperty
import net.gridtech.machine.model.IEntity
import net.gridtech.machine.model.entityClass.ModbusSlaveClass
import net.gridtech.machine.model.entityField.ModbusSlaveConnectionField
import net.gridtech.machine.model.property.entity.ModbusSlaveDefinitionDescription
import net.gridtech.machine.model.property.entity.SlaveAddress


class ModbusSlave(node: INode) : IEntity(node) {
    val modbusSlaveDefinition = ModbusSlaveDefinitionDescription(this)
    override fun getDescriptionProperty(): IBaseProperty<*, INode>? = modbusSlaveDefinition
    val slaveConnection
        get() = getEntityField<ModbusSlaveConnectionField>(source.nodeClassId, ModbusSlaveConnectionField.key).getFieldValue(source.id)

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
                        nodeClassId = modbusSlaveClass.source.id,
                        name = name,
                        alias = alias,
                        tags = tags,
                        externalNodeIdScope = emptyList(),
                        externalNodeClassTagScope = emptyList(),
                        description = SlaveAddress.empty()
                )?.apply {
                    getEntityField<ModbusSlaveConnectionField>(modbusSlaveClass.source.id, ModbusSlaveConnectionField.key)
                            .createFieldValue(this.id).update(false)
                }
    }
}