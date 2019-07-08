package net.gridtech.machine.model.entityClass

import net.gridtech.core.data.INodeClass
import net.gridtech.machine.model.IEntityClass
import net.gridtech.machine.model.entityField.SlaveConnectionField

class ModbusSlaveClass(nodeClass: INodeClass) : IEntityClass(nodeClass) {

    companion object {
        private val tags = listOf("modbus slave class")
        fun create(nodeClass: INodeClass): ModbusSlaveClass? =
                if (nodeClass.tags.containsAll(tags))
                    ModbusSlaveClass(nodeClass)
                else
                    null

        fun add(name: String, alias: String) =
                add(name, alias, false, tags, null)
                        ?.apply {
                            SlaveConnectionField.add(this.id)
                        }
    }
}