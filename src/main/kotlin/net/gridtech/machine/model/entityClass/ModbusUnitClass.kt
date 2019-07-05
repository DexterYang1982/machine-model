package net.gridtech.machine.model.entityClass

import net.gridtech.core.data.INodeClass
import net.gridtech.machine.model.IEntityClass
import net.gridtech.machine.model.property.ModbusDefinitionProperty


class ModbusUnitClass(nodeClass: INodeClass) : IEntityClass(nodeClass) {
    override fun getDescription(): Any? =
            modbusDefinitionProperty.current ?: ModbusDefinitionProperty.create()

    val modbusDefinitionProperty: ModbusDefinitionProperty = ModbusDefinitionProperty(this)

    companion object {
        private val tags = listOf("modbus unit class")
        fun create(nodeClass: INodeClass): ModbusUnitClass? =
                if (nodeClass.tags.containsAll(tags))
                    ModbusUnitClass(nodeClass)
                else
                    null

        fun add(name: String, alias: String) =
            add(name, alias, false, tags, ModbusDefinitionProperty.create())
    }
}
