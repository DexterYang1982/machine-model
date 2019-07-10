package net.gridtech.machine.model.entityClass

import net.gridtech.core.data.INodeClass
import net.gridtech.machine.model.IEntityClass
import net.gridtech.machine.model.entityField.ModbusSlaveConnectionField

class ModbusSlaveClass(id: String) : IEntityClass(id) {
    val connection = ModbusSlaveConnectionField(this)

    fun addNew(name: String, alias: String) {
        addNew(name, alias, tags, false)
    }

    companion object {
        val tags = listOf("modbus slave class")
        fun create(nodeClass: INodeClass): ModbusSlaveClass? =
                if (nodeClass.tags.containsAll(tags))
                    ModbusSlaveClass(nodeClass.id).apply { initialize(nodeClass) }
                else
                    null
    }
}