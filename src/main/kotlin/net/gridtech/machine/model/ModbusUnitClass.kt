package net.gridtech.machine.model



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