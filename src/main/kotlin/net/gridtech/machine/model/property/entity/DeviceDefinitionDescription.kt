package net.gridtech.machine.model.property.entity

import net.gridtech.core.data.INode
import net.gridtech.core.util.parse
import net.gridtech.machine.model.IBaseProperty
import net.gridtech.machine.model.IDependOnOthers
import net.gridtech.machine.model.entity.Device


class DeviceDefinitionDescription(private val device: Device)
    : IBaseProperty<DeviceDefinition, INode>({ parse(it.description) }, DeviceDefinition.empty())


data class DeviceDefinition(
        var status: List<ModbusRead>,
        var commands: List<ModbusWrite>,
        var errorStatusValue: List<DeviceStatusValue>
) {
    companion object {
        fun empty() = DeviceDefinition(
                emptyList(), emptyList(), emptyList()
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

data class DeviceStatusValue(
        var id: String,
        var deviceId: String,
        var modbusReadId: String,
        var valueDescriptionId: String
) : IDependOnOthers {
    override fun id(): String = id
    override fun dependence(): List<String> = listOf(deviceId, modbusReadId, valueDescriptionId)
}

data class DeviceCommandValue(
        var id: String,
        var deviceId: String,
        var modbusWriteId: String,
        var valueDescriptionId: String
) : IDependOnOthers {
    override fun id(): String = id
    override fun dependence(): List<String> = listOf(deviceId, modbusWriteId, valueDescriptionId)
}