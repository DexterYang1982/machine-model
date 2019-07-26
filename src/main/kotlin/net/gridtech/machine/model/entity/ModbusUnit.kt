package net.gridtech.machine.model.entity

import net.gridtech.core.data.INode
import net.gridtech.core.util.cast
import net.gridtech.machine.model.DataHolder
import net.gridtech.machine.model.EntityFieldValue
import net.gridtech.machine.model.IEntity
import net.gridtech.machine.model.entityClass.ModbusUnitClass
import net.gridtech.machine.model.property.field.ValueDescription


class ModbusUnit(id: String, entityClass: ModbusUnitClass) : IEntity<ModbusUnitClass>(id, entityClass) {

    fun getReadPointFieldValue(readPointId: String): EntityFieldValue<ValueDescription>? =
            entityClass.description.value?.read?.find { readPoint -> readPoint.id == readPointId }
                    ?.let { readPoint ->
                        getCustomFieldValue(readPoint.resultFieldId)
                    }

    fun getWritePointFieldValue(writePointId: String): EntityFieldValue<ValueDescription>? =
            entityClass.description.value?.write?.find { writePoint -> writePoint.id == writePointId }
                    ?.let { writePoint ->
                        getCustomFieldValue(writePoint.commandFieldId)
                    }

    fun getWritePointResultFieldValue(writePointId: String): EntityFieldValue<ValueDescription>? =
            entityClass.description.value?.write?.find { writePoint -> writePoint.id == writePointId }
                    ?.let { writePoint ->
                        getCustomFieldValue(writePoint.resultFieldId)
                    }


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
                    ModbusUnit(node.id, cast(DataHolder.instance.entityClassHolder[node.nodeClassId])!!).apply { initialize(node) }
                else
                    null

    }
}