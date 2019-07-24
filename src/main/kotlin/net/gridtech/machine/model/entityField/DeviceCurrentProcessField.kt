package net.gridtech.machine.model.entityField

import net.gridtech.core.util.cast
import net.gridtech.core.util.parse
import net.gridtech.machine.model.EntityFieldValue
import net.gridtech.machine.model.IEmbeddedEntityField
import net.gridtech.machine.model.IEntityClass


class DeviceCurrentProcessField(entityClass: IEntityClass) : IEmbeddedEntityField<TransactionProcess>(entityClass.id, key) {
    override fun defaultValue(): TransactionProcess = TransactionProcess.empty()
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

    override fun createFieldValue(entityId: String): EntityFieldValue<TransactionProcess> =
            object : EntityFieldValue<TransactionProcess>(entityId, id, { parse(it.value)!! }) {}
}