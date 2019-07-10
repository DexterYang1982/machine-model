package net.gridtech.machine.model.entityField

import net.gridtech.core.util.KEY_FIELD_SECRET
import net.gridtech.machine.model.EntityFieldValue
import net.gridtech.machine.model.IEmbeddedEntityField
import net.gridtech.machine.model.IEntityClass


class SecretField(entityClass: IEntityClass) : IEmbeddedEntityField<String>(entityClass.id, key) {
    override fun defaultValue(): String = "123456"
    override fun autoAddNew(): Boolean = false
    override fun autoInitValue(): Boolean = true

    companion object {
        const val key = KEY_FIELD_SECRET
    }

    override fun createFieldValue(entityId: String): EntityFieldValue<String> =
            object : EntityFieldValue<String>(entityId, id, { it.value }) {}
}