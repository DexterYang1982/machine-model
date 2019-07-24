package net.gridtech.machine.model.entityField

import net.gridtech.core.util.parse
import net.gridtech.machine.model.EntityFieldValue
import net.gridtech.machine.model.IEmbeddedEntityField
import net.gridtech.machine.model.IEntityClass

class DeviceHealthField(entityClass: IEntityClass) : IEmbeddedEntityField<Boolean>(entityClass.id, key) {
    override fun defaultValue(): Boolean = true
    override fun autoAddNew(): Boolean = true
    override fun autoInitValue(): Boolean = true
    override fun getFieldKey(): String = key
    override fun addNew() {
        addNew(
                "device healthy",
                "isHealthy",
                emptyList(),
                true,
                null)
    }

    companion object {
        const val key = "device-healthy"
    }

    override fun createFieldValue(entityId: String): EntityFieldValue<Boolean> =
            object : EntityFieldValue<Boolean>(entityId, id, { parse(it.value)!! }) {}
}