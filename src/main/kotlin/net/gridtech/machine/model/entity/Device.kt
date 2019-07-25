package net.gridtech.machine.model.entity

import net.gridtech.core.data.INode
import net.gridtech.core.util.cast
import net.gridtech.machine.model.DataHolder
import net.gridtech.machine.model.IEntity
import net.gridtech.machine.model.entityClass.DeviceClass
import net.gridtech.machine.model.entityField.CustomField
import net.gridtech.machine.model.property.entity.DeviceDefinitionDescription


class Device(id: String, entityClass: DeviceClass) : IEntity<DeviceClass>(id, entityClass) {
    override val description = DeviceDefinitionDescription(this)
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
        val tags = listOf("device")
        fun create(node: INode): Device? =
                if (node.tags.containsAll(tags))
                    Device(node.id, cast(DataHolder.instance.entityClassHolder[node.nodeClassId])!!).apply { initialize(node) }
                else
                    null
    }

    fun executeCommand(commandId: String, valueDescriptionId: String, session: String): Boolean? =
            description.value?.commands?.find { it.id == commandId }?.let { command ->
                DataHolder.instance.entityHolder[command.modbusUnitId]?.takeIf { it is ModbusUnit }?.let { modbusUnit ->
                    (modbusUnit as ModbusUnit).entityClass.description.value?.write?.find { it -> it.id == command.writePointId }?.let { writePoint ->
                        DataHolder.instance.entityFieldHolder[writePoint.commandFieldId]?.takeIf { it is CustomField }?.let {
                            (it as CustomField).description.value?.valueDescriptions?.find { valueDescription -> valueDescription.id == valueDescriptionId }
                        }?.let { valueDescription ->
                            modbusUnit.getCustomFieldValue(writePoint.commandFieldId)?.let { customFieldValue ->
                                customFieldValue.update(valueDescription.valueExp, session)
                                true
                            }
                        }
                    }
                }
            }
}