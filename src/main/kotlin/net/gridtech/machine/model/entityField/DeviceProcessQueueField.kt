package net.gridtech.machine.model.entityField

import net.gridtech.core.util.parse
import net.gridtech.machine.model.EntityFieldValue
import net.gridtech.machine.model.IEmbeddedEntityField
import net.gridtech.machine.model.IEntityClass


class DeviceProcessQueueField(entityClass: IEntityClass) : IEmbeddedEntityField<List<ProcessRuntime>>(entityClass.id, key) {
    override fun defaultValue(): List<ProcessRuntime> = emptyList()
    override fun autoAddNew(): Boolean = true
    override fun autoInitValue(): Boolean = true
    override fun getFieldKey(): String = key

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

    override fun createFieldValue(entityId: String): EntityFieldValue<List<ProcessRuntime>> =
            object : EntityFieldValue<List<ProcessRuntime>>(entityId, id, { parse(it.value)!! }) {}
}