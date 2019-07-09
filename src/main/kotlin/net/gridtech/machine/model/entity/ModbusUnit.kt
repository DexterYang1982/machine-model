package net.gridtech.machine.model.entity

import net.gridtech.core.data.INode
import net.gridtech.core.util.generateId
import net.gridtech.machine.model.IEntity
import net.gridtech.machine.model.entityClass.ModbusUnitClass


class ModbusUnit(node: INode) : IEntity(node) {

    val modbusDefinition = getEntityClass<ModbusUnitClass>().modbusUnitDescription

    companion object {
        val tags = listOf("modbus unit")
        fun create(node: INode): ModbusUnit? =
                if (node.tags.containsAll(tags))
                    ModbusUnit(node)
                else
                    null

        fun add(modbusUnitClass: ModbusUnitClass, parentId: String, name: String, alias: String) =
                add(
                        id = generateId(),
                        parentId = parentId,
                        nodeClassId = modbusUnitClass.source.id,
                        name = name,
                        alias = alias,
                        tags = tags,
                        externalNodeIdScope = emptyList(),
                        externalNodeClassTagScope = emptyList(),
                        description = null
                )
    }
}