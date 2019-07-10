package net.gridtech.machine.model.entityClass

import net.gridtech.core.data.INodeClass
import net.gridtech.machine.model.IBaseProperty
import net.gridtech.machine.model.IEntityClass
import net.gridtech.machine.model.property.entityClass.ModbusUnitDescriptionProperty


class ModbusUnitClass(id: String) : IEntityClass(id) {
    val description = ModbusUnitDescriptionProperty(this)
    override fun getDescriptionProperty(): IBaseProperty<*, INodeClass>? = description

    fun addNew(name: String, alias: String) =
            addNew(name, alias, tags, false)

    companion object {
        val tags = listOf("modbus unit class")
        fun create(nodeClass: INodeClass): ModbusUnitClass? =
                if (nodeClass.tags.containsAll(tags))
                    ModbusUnitClass(nodeClass.id).apply { initialize(nodeClass) }
                else
                    null

    }
}
