package net.gridtech.machine.model.property.entity

import net.gridtech.core.data.INode
import net.gridtech.core.util.parse
import net.gridtech.machine.model.EntityWrite
import net.gridtech.machine.model.IBaseProperty
import net.gridtech.machine.model.IDependOnOthers
import net.gridtech.machine.model.ReadCondition
import net.gridtech.machine.model.entity.Device


class DeviceDefinitionDescription(private val device: Device)
    : IBaseProperty<DeviceDefinition, INode>({ parse(it.description) }, DeviceDefinition.empty())


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
        var name:String,
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