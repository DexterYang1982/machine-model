package net.gridtech.machine.model.property.entity

import net.gridtech.core.data.INode
import net.gridtech.core.util.APIExceptionEnum
import net.gridtech.core.util.generateId
import net.gridtech.core.util.parse
import net.gridtech.machine.model.*
import net.gridtech.machine.model.entity.Device


class DeviceDefinitionDescription(private val device: Device)
    : IBaseProperty<DeviceDefinition, INode>({
    parse(it.description)
}, DeviceDefinition.empty()) {

    fun addStatus(modbusRead: ModbusRead) =
            value?.let {
                modbusRead.id = generateId()
                val newStatus = it.status.toMutableList()
                newStatus.add(modbusRead)
                device.updateDescription(it.copy(status = newStatus))
            }

    fun updateStatus(modbusRead: ModbusRead) =
            value?.takeIf { it.status.find { s -> s.id == modbusRead.id } != null }
                    ?.let {
                        device.updateDescription(it.copy(status = it.status.map { s -> if (s.id == modbusRead.id) modbusRead else s }))
                    }

    fun deleteStatus(id: String) =
            APIExceptionEnum.ERR10_CAN_NOT_BE_DELETED.assert(DataHolder.instance.checkDependency(id)).let {
                value?.takeIf { it.status.find { s -> s.id == id } != null }
                        ?.let {
                            device.updateDescription(it.copy(status = it.status.filter { s -> s.id != id }))
                        }
            }

    fun addCommand(modbusWrite: ModbusWrite) =
            value?.let {
                modbusWrite.id = generateId()
                val newCommands = it.commands.toMutableList()
                newCommands.add(modbusWrite)
                device.updateDescription(it.copy(commands = newCommands))
            }

    fun updateCommand(modbusWrite: ModbusWrite) =
            value?.takeIf { it.commands.find { c -> c.id == modbusWrite.id } != null }
                    ?.let {
                        device.updateDescription(it.copy(commands = it.commands.map { c -> if (c.id == modbusWrite.id) modbusWrite else c }))
                    }

    fun deleteCommand(id: String) =
            APIExceptionEnum.ERR10_CAN_NOT_BE_DELETED.assert(DataHolder.instance.checkDependency(id)).let {
                value?.takeIf { it.commands.find { c -> c.id == id } != null }
                        ?.let {
                            device.updateDescription(it.copy(commands = it.commands.filter { c -> c.id != id }))
                        }
            }

    fun addProcess(deviceProcess: DeviceProcess) =
            value?.let {
                deviceProcess.id = generateId()
                val newProcesses = it.processes.toMutableList()
                newProcesses.add(deviceProcess)
                device.updateDescription(it.copy(processes = newProcesses))
            }

    fun updateProcess(deviceProcess: DeviceProcess) =
            value?.takeIf { it.processes.find { p -> p.id == deviceProcess.id } != null }
                    ?.let {
                        device.updateDescription(it.copy(processes = it.processes.map { p -> if (p.id == deviceProcess.id) deviceProcess else p }))
                    }

    fun deleteProcess(id: String) =
            APIExceptionEnum.ERR10_CAN_NOT_BE_DELETED.assert(DataHolder.instance.checkDependency(id)).let {
                value?.takeIf { it.processes.find { p -> p.id == id } != null }
                        ?.let {
                            device.updateDescription(it.copy(processes = it.processes.filter { p -> p.id != id }))
                        }
            }

    fun updateErrorCondition(errorCondition: ReadCondition) =
            value?.let {
                device.updateDescription(it.copy(errorCondition = errorCondition))
            }
}


data class DeviceDefinition(
        var status: List<ModbusRead>,
        var commands: List<ModbusWrite>,
        var processes: List<DeviceProcess>,
        var errorCondition: ReadCondition
) {
    companion object {
        fun empty() = DeviceDefinition(
                emptyList(), emptyList(), emptyList(), ReadCondition.empty()
        )
    }
}

data class ModbusRead(
        var id: String,
        var modbusUnitId: String,
        var readPointId: String
) : IDependOnOthers {
    override fun id(): String = id
    override fun dependence(): List<String> = listOf(modbusUnitId, readPointId)
}

data class ModbusWrite(
        var id: String,
        var modbusUnitId: String,
        var writePointId: String
) : IDependOnOthers {
    override fun id(): String = id
    override fun dependence(): List<String> = listOf(modbusUnitId, writePointId)
}

data class DeviceProcess(
        var id: String,
        var name: String,
        var steps: List<DeviceProcessStep>
)

data class DeviceProcessStep(
        var id: String,
        var name: String,
        var executeCondition: ReadCondition,
        var execute: EntityWrite,
        var endCondition: ReadCondition,
        var timeout: Long
)