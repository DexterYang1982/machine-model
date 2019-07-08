package net.gridtech.machine.model.property

import net.gridtech.core.data.INodeClass
import net.gridtech.core.util.parse
import net.gridtech.machine.model.IBaseProperty
import net.gridtech.machine.model.entityClass.ModbusSlaveClass


class SlaveAddressDescription(private val modbusSlaveClass: ModbusSlaveClass)
    : IBaseProperty<SlaveAddress, INodeClass>({ parse(it.description) }, SlaveAddress.empty()) {

}

data class SlaveAddress(
        var ip: String,
        var port: Int
) {
    companion object {
        fun empty() = SlaveAddress("127.0.0.1", 502)
    }
}