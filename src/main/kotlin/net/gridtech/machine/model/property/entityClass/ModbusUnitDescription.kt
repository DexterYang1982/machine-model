package net.gridtech.machine.model.property.entityClass

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

    fun addReadPoint(readPoint: ReadPoint) =
            value?.let {
                readPoint.id = generateId()
                val newReadPoints = it.read.toMutableList()
                newReadPoints.add(readPoint)
                modbusUnitClass.updateDescription(it.copy(read = newReadPoints))
            }

    fun updateReadPoint(readPoint: ReadPoint) =
            value?.takeIf { it.read.find { r -> r.id == readPoint.id } != null }
                    ?.let {
                        modbusUnitClass.updateDescription(it.copy(read = it.read.map { r -> if (r.id == readPoint.id) readPoint else r }))
                    }

    fun deleteReadPoint(id: String) =
            APIExceptionEnum.ERR10_CAN_NOT_BE_DELETED.assert(DataHolder.instance.checkDependency(id)).let {
                value?.takeIf { it.read.find { r -> r.id == id } != null }
                        ?.let {
                            modbusUnitClass.updateDescription(it.copy(read = it.read.filter { r -> r.id != id }))
                        }
            }

    fun addWritePoint(writePoint: WritePoint) =
            value?.let {
                writePoint.id = generateId()
                val newWritePoints = it.write.toMutableList()
                newWritePoints.add(writePoint)
                modbusUnitClass.updateDescription(it.copy(write = newWritePoints))
            }

    fun updateWritePoint(writePoint: WritePoint) =
            value?.takeIf { it.write.find { w -> w.id == writePoint.id } != null }
                    ?.let {
                        modbusUnitClass.updateDescription(it.copy(write = it.write.map { w -> if (w.id == writePoint.id) writePoint else w }))
                    }

    fun deleteWritePoint(id: String) =
            APIExceptionEnum.ERR10_CAN_NOT_BE_DELETED.assert(DataHolder.instance.checkDependency(id)).let {
                value?.takeIf { it.write.find { w -> w.id == id } != null }
                        ?.let {
                            modbusUnitClass.updateDescription(it.copy(write = it.write.filter { w -> w.id != id }))
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
        var sessionFollowWritePoints: List<String>
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


enum class CommandResult {
    ACCEPTED,
    OFFLINE,
    EXPIRED,
    EXCEPTION
}


enum class CommandType {
    INSTANT,
    PERSISTENT
}