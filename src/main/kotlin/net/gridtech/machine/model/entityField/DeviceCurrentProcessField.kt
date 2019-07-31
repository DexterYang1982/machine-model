package net.gridtech.machine.model.entityField

import net.gridtech.core.util.parse
import net.gridtech.machine.model.EntityFieldValue
import net.gridtech.machine.model.IEmbeddedEntityField
import net.gridtech.machine.model.IEntityClass


class DeviceCurrentProcessField(entityClass: IEntityClass) : IEmbeddedEntityField<ProcessRuntime>(entityClass.id, key) {
    override fun defaultValue(): ProcessRuntime = ProcessRuntime.empty()
    override fun autoAddNew(): Boolean = true
    override fun autoInitValue(): Boolean = true

    override fun getFieldKey(): String = key
    override fun addNew() {
        addNew(
                "device current process",
                "deviceCurrentProcess",
                emptyList(),
                true,
                null)
    }

    companion object {
        const val key = "current-process"
    }

    override fun createFieldValue(entityId: String): EntityFieldValue<ProcessRuntime> =
            object : EntityFieldValue<ProcessRuntime>(entityId, id, { parse(it.value)!! }) {}
}


data class ProcessRuntime(
        var transactionId: String?,
        var transactionPhaseId: String?,
        var transactionSession: String?,
        var transactionPhaseSession: String,
        var tunnelId: String?,
        var deviceId: String,
        var deviceProcessId: String,
        var stepRuntime: List<StepRuntime>,
        var initTime: Long,
        var delay: Long,
        var state: ProcessState
) {
    companion object {
        fun empty() = ProcessRuntime(
                null,
                null,
                null,
                "",
                null,
                "",
                "",
                emptyList(),
                -1,
                -1,
                ProcessState.FINISHED
        )
    }

    fun session(): String =
            "${transactionSession ?: ""}$transactionPhaseSession"
}

data class StepRuntime(
        var stepId: String,
        var state: StepState,
        var startTime: Long?,
        var endTime: Long?
)

enum class StepState {
    RUNNING,
    ERROR,
    TIMEOUT,
    FINISHED
}
