package net.gridtech.machine.model.entityField

import net.gridtech.core.util.cast
import net.gridtech.machine.model.EntityFieldValue
import net.gridtech.machine.model.IEmbeddedEntityField
import net.gridtech.machine.model.IEntityClass


class CabinEmptyField(entityClass: IEntityClass) : IEmbeddedEntityField<Boolean>(entityClass.id, key) {
    override fun defaultValue(): Boolean = false
    override fun autoAddNew(): Boolean = true
    override fun autoInitValue(): Boolean = true

    override fun addNew() {
        addNew(
                "cabin is empty",
                "isEmpty",
                emptyList(),
                true,
                null)
    }

    companion object {
        const val key = "cabin-is-empty"
    }

    override fun createFieldValue(entityId: String): EntityFieldValue<Boolean> =
            object : EntityFieldValue<Boolean>(entityId, id, { cast(it.value)!! }) {}
}