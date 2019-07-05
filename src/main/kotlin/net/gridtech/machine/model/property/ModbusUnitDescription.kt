package net.gridtech.machine.model.property

import net.gridtech.core.data.INodeClass
import net.gridtech.core.util.APIExceptionEnum
import net.gridtech.core.util.generateId
import net.gridtech.core.util.parse
import net.gridtech.machine.model.DataHolder
import net.gridtech.machine.model.IBaseProperty
import net.gridtech.machine.model.IDependOnOthers
import net.gridtech.machine.model.entityClass.ModbusUnitClass

class ModbusUnitDescriptionProperty(private val modbusUnitClass: ModbusUnitClass)
    : IBaseProperty<ModbusUnitDescription, INodeClass>({ parse(it.description) }, ModbusUnitDescription.empty()) {

    override fun deleteOldDependency() {
        value?.read?.forEach { DataHolder.instance.deleteDependency(it.id) }
        value?.write?.forEach { DataHolder.instance.deleteDependency(it.id) }
    }

    override fun addNewDependency() {
        value?.read?.forEach { DataHolder.instance.addDependency(it) }
        value?.write?.forEach { DataHolder.instance.addDependency(it) }
    }

    fun addReadPoint(readPoint: ReadPoint) {
        readPoint.id = generateId()
        value?.apply {
            val newReadPoints = read.toMutableList()
            newReadPoints.add(readPoint)
            modbusUnitClass.updateDescription(this.copy(read = newReadPoints))
        }
    }

    fun updateReadPoint(readPoint: ReadPoint) {
        value?.apply {
            if (read.find { it.id == readPoint.id } != null) {
                modbusUnitClass.updateDescription(this.copy(read = read.map { if (it.id == readPoint.id) readPoint else it }))
            }
        }
    }

    fun deleteReadPoint(id: String) {
        APIExceptionEnum.ERR10_CAN_NOT_BE_DELETED.assert(DataHolder.instance.checkDependency(id))
        value?.apply {
            if (read.find { it.id == id } != null) {
                modbusUnitClass.updateDescription(this.copy(read = read.filter { it.id != id }))
            }
        }
    }

    fun addWritePoint(writePoint: WritePoint) {
        writePoint.id = generateId()

        value?.apply {
            val newWritePoints = write.toMutableList()
            newWritePoints.add(writePoint)
            modbusUnitClass.updateDescription(this.copy(write = newWritePoints))
        }
    }

    fun updateWritePoint(writePoint: WritePoint) {
        value?.apply {
            if (write.find { it.id == writePoint.id } != null) {
                modbusUnitClass.updateDescription(this.copy(write = write.map { if (it.id == writePoint.id) writePoint else it }))
            }
        }
    }

    fun deleteWritePoint(id: String) {
        APIExceptionEnum.ERR10_CAN_NOT_BE_DELETED.assert(DataHolder.instance.checkDependency(id))
        value?.apply {
            if (write.find { it.id == id } != null) {
                modbusUnitClass.updateDescription(this.copy(write = write.filter { it.id != id }))
            }
        }
    }
}

data class ModbusUnitDescription(
        val read: List<ReadPoint>,
        val write: List<WritePoint>
) {
    companion object {
        fun empty() = ModbusUnitDescription(emptyList(), emptyList())
    }
}

data class ReadPoint(
        var id: String,
        var name: String,
        var point: Point,
        var resultFieldId: String,
        var sessionFollowWritePointKeys: List<String>
) : IDependOnOthers {
    override fun id(): String = id
    override fun dependence(): List<String> = listOf(resultFieldId)
}

data class WritePoint(
        var id: String,
        var name: String,
        var point: Point,
        var expired: Long,
        var commandFieldId: String,
        var resultFieldId: String,
        var commandType: CommandType
) : IDependOnOthers {
    override fun id(): String = id
    override fun dependence(): List<String> = listOf(commandFieldId, resultFieldId)
}


data class Point(
        var position: Int,
        var quantity: Int,
        var memoryType: MemoryType
)

enum class MemoryType {
    HOLDING_REGISTER,
    COIL_STATUS,
    INPUT_STATUS,
    INPUT_REGISTER
}

enum class CommandType {
    INSTANT,
    PERSISTENT
}