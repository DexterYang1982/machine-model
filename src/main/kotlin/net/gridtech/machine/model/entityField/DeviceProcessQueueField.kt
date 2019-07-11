package net.gridtech.machine.model.entityField

import net.gridtech.core.util.cast
import net.gridtech.machine.model.EntityFieldValue
import net.gridtech.machine.model.IEmbeddedEntityField
import net.gridtech.machine.model.IEntityClass


class DeviceProcessQueueField(entityClass: IEntityClass) : IEmbeddedEntityField<List<TransactionProcess>>(entityClass.id, key) {
    override fun defaultValue(): List<TransactionProcess> = emptyList()
    override fun autoAddNew(): Boolean = true
    override fun autoInitValue(): Boolean = true

    override fun addNew() {
        addNew(
                "device process queue",
                "deviceProcessQueue",
                emptyList(),
                true,
                null)
    }

    companion object {
        const val key = "process-queue"
    }

    override fun createFieldValue(entityId: String): EntityFieldValue<List<TransactionProcess>> =
            object : EntityFieldValue<List<TransactionProcess>>(entityId, id, { cast(it.value)!! }) {}
}