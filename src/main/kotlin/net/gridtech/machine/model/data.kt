package net.gridtech.machine.model

data class EntityRead(
        var id: String,
        var entityId: String,
        var targetType: ReadTargetType,
        var targetId: String,
        var valueDescriptionId: String
) : IDependOnOthers {
    override fun id(): String = id
    override fun dependence(): List<String> = listOf(entityId, targetId, valueDescriptionId)
}

enum class ReadTargetType {
    DEVICE_STATUS,
    CUSTOM_FIELD
}

data class ReadCondition(
        var matchAll: Boolean,
        var reads: List<EntityRead>
) {
    companion object {
        fun empty() = ReadCondition(true, emptyList())
    }
}

data class EntityWrite(
        var id: String,
        var entityId: String,
        var targetType: WriteTargetType,
        var targetId: String,
        var valueDescriptionId: String
) : IDependOnOthers {
    companion object {
        fun empty() = EntityWrite(
                "",
                "",
                WriteTargetType.DEVICE_COMMAND,
                "",
                ""

        )
    }

    override fun id(): String = id
    override fun dependence(): List<String> = listOf(entityId, targetId, valueDescriptionId)
}

enum class WriteTargetType {
    DEVICE_COMMAND,
    CUSTOM_FIELD
}

data class Trigger(
        var id: String,
        var name: String,
        var condition: ReadCondition,
        var delay: Long,
        var writes: List<EntityWrite>
)