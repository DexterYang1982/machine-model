package net.gridtech.machine.model.property.entity

import net.gridtech.core.data.INode
import net.gridtech.core.util.parse
import net.gridtech.machine.model.IBaseProperty
import net.gridtech.machine.model.entity.ModbusSlave


class ModbusSlaveDefinitionDescription(private val modbusSlave: ModbusSlave)
    : IBaseProperty<SlaveAddress, INode>({ parse(it.description) }, SlaveAddress.empty()) {
    fun updateSlaveAddress(slaveAddress: SlaveAddress) {
        modbusSlave.updateDescription(slaveAddress)
    }
}

data class SlaveAddress(
        var ip: String,
        var port: Int
) {
    companion object {
        fun empty() = SlaveAddress("127.0.0.1", 502)
    }
}