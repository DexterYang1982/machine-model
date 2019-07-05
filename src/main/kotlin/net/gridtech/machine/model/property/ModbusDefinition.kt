package net.gridtech.machine.model.property

import net.gridtech.core.data.INodeClass
import net.gridtech.core.util.APIExceptionEnum
import net.gridtech.core.util.generateId
import net.gridtech.core.util.parse
import net.gridtech.machine.model.DataHolder
import net.gridtech.machine.model.IDependOnOthers
import net.gridtech.machine.model.IProperty
import net.gridtech.machine.model.entityClass.ModbusUnitClass

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

    override fun deleteOldDependency() {
        current?.read?.forEach { DataHolder.instance.deleteDependency(it.id) }
        current?.write?.forEach { DataHolder.instance.deleteDependency(it.id) }
    }

    override fun addNewDependency() {
        current?.read?.forEach { DataHolder.instance.addDependency(it) }
        current?.write?.forEach { DataHolder.instance.addDependency(it) }
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
        APIExceptionEnum.ERR10_CAN_NOT_BE_DELETED.assert(DataHolder.instance.checkDependency(id))
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
        APIExceptionEnum.ERR10_CAN_NOT_BE_DELETED.assert(DataHolder.instance.checkDependency(id))
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