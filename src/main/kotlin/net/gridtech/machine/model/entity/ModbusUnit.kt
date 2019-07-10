package net.gridtech.machine.model.entity

import net.gridtech.core.data.INode
import net.gridtech.machine.model.IEntity
import net.gridtech.machine.model.entityClass.ModbusUnitClass


class ModbusUnit : IEntity<ModbusUnitClass> {
    constructor(node: INode) : super(node)
    constructor(id: String, t: ModbusUnitClass) : super(id, t)

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
        val tags = listOf("modbus unit")
        fun create(node: INode): ModbusUnit? =
                if (node.tags.containsAll(tags))
                    ModbusUnit(node)
                else
                    null

    }
}