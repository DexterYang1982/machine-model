package net.gridtech.machine.model.entity

import net.gridtech.core.data.INode
import net.gridtech.core.util.generateId
import net.gridtech.machine.model.IEntity
import net.gridtech.machine.model.entityClass.ModbusSlaveClass
import net.gridtech.machine.model.entityField.SlaveConnectionField


class ModbusSlave(node: INode) : IEntity(node) {
    val slaveConnection
        get() = getEntityField<SlaveConnectionField>(source.nodeClassId, SlaveConnectionField.key).getFieldValue(source.id)

    companion object {
        private val tags = listOf("modbus slave")
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
                        description = null
                )?.apply {
                    getEntityField<SlaveConnectionField>(modbusSlaveClass.source.id, SlaveConnectionField.key)
                            .createFieldValue(this.id).update(false)
                }
    }
}