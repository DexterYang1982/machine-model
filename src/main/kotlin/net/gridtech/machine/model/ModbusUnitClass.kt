package net.gridtech.machine.model

import net.gridtech.core.data.INodeClass


class ModbusUnitClass(nodeClass: INodeClass) : IEntityClass(nodeClass) {
    override fun getDescription(): Any? =
            modbusDefinitionProperty.current ?: ModbusDefinitionProperty.create()

    val modbusDefinitionProperty: ModbusDefinitionProperty = ModbusDefinitionProperty(this)

    companion object {
        val tags = listOf("modbus unit class")
        fun match(nodeClass: INodeClass): Boolean = nodeClass.tags.containsAll(tags)

        fun create(name: String, alias: String) {
            create(name, alias, false, tags, ModbusDefinitionProperty.create())
        }
    }
}
