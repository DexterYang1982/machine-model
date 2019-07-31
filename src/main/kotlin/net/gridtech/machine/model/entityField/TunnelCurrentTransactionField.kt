package net.gridtech.machine.model.entityField

import net.gridtech.core.util.parse
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
            object : EntityFieldValue<CurrentTransaction>(entityId, id, { parse(it.value)!! }) {}
}

data class CurrentTransaction(
        var transactionId: String,
        var transactionSession: String,
        var transactionProcesses: List<ProcessRuntime>
) {
    companion object {
        fun empty() = CurrentTransaction("", "", emptyList())
    }
}

enum class ProcessState {
    INIT,
    QUEUED,
    WAITING,
    RUNNING,
    ERROR,
    FINISHED
}