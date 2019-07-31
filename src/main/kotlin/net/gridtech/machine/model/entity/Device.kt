package net.gridtech.machine.model.entity

import net.gridtech.core.data.INode
import net.gridtech.core.util.cast
import net.gridtech.core.util.currentTime
import net.gridtech.machine.model.DataHolder
import net.gridtech.machine.model.IEntity
import net.gridtech.machine.model.entityClass.DeviceClass
import net.gridtech.machine.model.entityField.CustomField
import net.gridtech.machine.model.entityField.ProcessRuntime
import net.gridtech.machine.model.entityField.ProcessState
import net.gridtech.machine.model.property.entity.DeviceDefinitionDescription
import net.gridtech.machine.model.property.entity.DeviceProcess
import net.gridtech.machine.model.property.entity.ModbusRead
import net.gridtech.machine.model.property.entity.ModbusWrite


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

    fun getStatusById(statusId: String): ModbusRead? =
            description.value?.status?.find { status -> status.id == statusId }

    fun getCommandById(commandId: String): ModbusWrite? =
            description.value?.commands?.find { command -> command.id == commandId }

    fun getProcessById(processId: String): DeviceProcess? =
            description.value?.processes?.find { process -> process.id == processId }

    fun addNewProcessQueue(processQueue: List<ProcessRuntime>) {
        val processQueueField = entityClass.processQueue.getFieldValue(this)
        val currentQueue = processQueueField.value?.toMutableList() ?: mutableListOf()
        currentQueue.addAll(processQueue)
        processQueueField.update(currentQueue, processQueue.first().transactionSession)
    }

    fun executeProcess(processId: String, session: String): Boolean? =
            getProcessById(processId)?.let { process ->
                entityClass.currentProcess.getFieldValue(this).update(ProcessRuntime(
                        transactionId = null,
                        transactionPhaseId = null,
                        transactionSession = null,
                        transactionPhaseSession = session,
                        tunnelId = null,
                        deviceId = this.id,
                        deviceProcessId = process.id,
                        initTime = currentTime(),
                        delay = 0,
                        stepRuntime = emptyList(),
                        state = ProcessState.QUEUED
                ), session)
                true
            } ?: false

    fun executeCommand(commandId: String, valueDescriptionId: String, session: String): Boolean? =
            getCommandById(commandId)?.let { command ->
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