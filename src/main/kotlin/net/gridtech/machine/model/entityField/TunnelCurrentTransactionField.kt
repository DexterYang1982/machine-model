package net.gridtech.machine.model.entityField

import net.gridtech.core.util.cast
import net.gridtech.machine.model.EntityFieldValue
import net.gridtech.machine.model.IEmbeddedEntityField
import net.gridtech.machine.model.IEntityClass


class TunnelCurrentTransactionField(entityClass: IEntityClass) : IEmbeddedEntityField<CurrentTransaction>(entityClass.id, key) {
    override fun defaultValue(): CurrentTransaction = CurrentTransaction.empty()
    override fun autoAddNew(): Boolean = true
    override fun autoInitValue(): Boolean = true
    override fun getFieldKey(): String = key

    override fun addNew() {
        addNew(
                "current transaction",
                "currentTransaction",
                emptyList(),
                true,
                null)
    }

    companion object {
        const val key = "current-transaction"
    }

    override fun createFieldValue(entityId: String): EntityFieldValue<CurrentTransaction> =
            object : EntityFieldValue<CurrentTransaction>(entityId, id, { cast(it.value)!! }) {}
}

data class CurrentTransaction(
        var transactionSession: String,
        var transactionId: String,
        var transactionProcesses: List<TransactionProcess>
) {
    companion object {
        fun empty() = CurrentTransaction("", "", emptyList())
    }
}

data class TransactionProcess(
        var transactionSession: String,
        var transactionPhaseSession: String,
        var tunnelId: String,
        var transactionId: String,
        var transactionPhaseId: String,
        var deviceId: String,
        var deviceProcessId: String,
        var state: ProcessState
) {
    companion object {
        fun empty() = TransactionProcess(
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                ProcessState.FINISHED
        )
    }
}

enum class ProcessState {
    INIT,
    QUEUED,
    RUNNING,
    FINISHED
}