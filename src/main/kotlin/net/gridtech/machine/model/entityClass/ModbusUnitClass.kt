package net.gridtech.machine.model.entityClass

import net.gridtech.core.data.INodeClass
import net.gridtech.machine.model.IBaseProperty
import net.gridtech.machine.model.IEntityClass
import net.gridtech.machine.model.property.entityClass.ModbusUnitDescription
import net.gridtech.machine.model.property.entityClass.ModbusUnitDescriptionProperty


class ModbusUnitClass(nodeClass: INodeClass) : IEntityClass(nodeClass) {
    val modbusUnitDescription = ModbusUnitDescriptionProperty(this)
    override fun getDescriptionProperty(): IBaseProperty<*, INodeClass>? = modbusUnitDescription

    companion object {
        val tags = listOf("modbus unit class")
        fun create(nodeClass: INodeClass): ModbusUnitClass? =
                if (nodeClass.tags.containsAll(tags))
                    ModbusUnitClass(nodeClass)
                else
                    null

        fun add(name: String, alias: String) =
                add(name, alias, false, tags, ModbusUnitDescription.empty())
    }
}
