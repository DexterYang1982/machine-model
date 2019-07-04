package net.gridtech.machine.model

import net.gridtech.core.data.INodeClass
import net.gridtech.core.util.generateId
import net.gridtech.core.util.parse

class ModbusDefinitionProperty(private val modbusUnitClass: ModbusUnitClass) : IProperty<ModbusDefinition, INodeClass>(
        modbusUnitClass.updatePublisher,
        modbusUnitClass.deletePublisher,
        {
            parse(it.description)
        }
) {
    companion object {
        fun create() = ModbusDefinition.empty()
    }

    fun addReadPoint(readPoint: ReadPoint) {
        readPoint.id = generateId()
        val newReadPoints = current?.read?.toMutableList()
        newReadPoints?.add(readPoint)
        newReadPoints?.let {
            current?.copy(
                    read = it
            )
        }?.apply {
            modbusUnitClass.updateDescription(this)
        }
    }

    fun updateReadPoint(readPoint: ReadPoint) {
        if (current?.read?.find { it.id == readPoint.id } != null) {
            val newReadPoints = current?.read?.map { if (it.id == readPoint.id) readPoint else it }
            newReadPoints?.let {
                current?.copy(
                        read = it
                )
            }?.apply {
                modbusUnitClass.updateDescription(this)
            }
        }
    }

    fun deleteReadPoint(id: String) {
        if (current?.read?.find { it.id == id } != null) {
            val newReadPoints = current?.read?.filter { it.id != id }
            newReadPoints?.let {
                current?.copy(
                        read = it
                )
            }?.apply {
                modbusUnitClass.updateDescription(this)
            }
        }
    }

    fun addWritePoint(writePoint: WritePoint) {
        writePoint.id = generateId()
        val newWritePoints = current?.write?.toMutableList()
        newWritePoints?.add(writePoint)
        newWritePoints?.let {
            current?.copy(
                    write = it
            )
        }?.apply {
            modbusUnitClass.updateDescription(this)
        }
    }

    fun updateWritePoint(writePoint: WritePoint) {
        if (current?.write?.find { it.id == writePoint.id } != null) {
            val newWritePoints = current?.write?.map { if (it.id == writePoint.id) writePoint else it }
            newWritePoints?.let {
                current?.copy(
                        write = it
                )
            }?.apply {
                modbusUnitClass.updateDescription(this)
            }
        }
    }

    fun deleteWritePoint(id: String) {
        if (current?.write?.find { it.id == id } != null) {
            val newWritePoints = current?.write?.filter { it.id != id }
            newWritePoints?.let {
                current?.copy(
                        write = it
                )
            }?.apply {
                modbusUnitClass.updateDescription(this)
            }
        }
    }
}

data class ModbusDefinition(
        val read: List<ReadPoint>,
        val write: List<WritePoint>
) {
    companion object {
        fun empty() = ModbusDefinition(emptyList(), emptyList())
    }
}

data class ReadPoint(
        var id: String,
        var name: String,
        var point: Point,
        var resultFieldId: String,
        var sessionFollowWritePointKeys: List<String>
)

data class WritePoint(
        var id: String,
        var name: String,
        var point: Point,
        var expired: Long,
        var commandFieldId: String,
        var resultFieldId: String,
        var commandType: CommandType
)


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