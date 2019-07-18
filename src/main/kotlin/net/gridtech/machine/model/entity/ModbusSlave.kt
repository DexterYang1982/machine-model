package net.gridtech.machine.model.entity

import net.gridtech.core.data.INode
import net.gridtech.core.util.cast
import net.gridtech.machine.model.DataHolder
import net.gridtech.machine.model.IEntity
import net.gridtech.machine.model.entityClass.ModbusSlaveClass
import net.gridtech.machine.model.property.entity.ModbusSlaveDefinitionDescription


class ModbusSlave(id: String, entityClass: ModbusSlaveClass) : IEntity<ModbusSlaveClass>(id, entityClass) {
    override val description = ModbusSlaveDefinitionDescription(this)

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
        val tags = listOf("modbus slave")
        fun create(node: INode): ModbusSlave? =
                if (node.tags.containsAll(tags))
                    ModbusSlave(node.id, cast(DataHolder.instance.entityClassHolder[node.nodeClassId])!!).apply { initialize(node) }
                else
                    null
    }
}