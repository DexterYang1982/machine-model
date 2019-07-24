package net.gridtech.machine.model.entityField

import net.gridtech.core.util.parse
import net.gridtech.machine.model.EntityFieldValue
import net.gridtech.machine.model.IEmbeddedEntityField
import net.gridtech.machine.model.IEntityClass


class CabinStorageField(entityClass: IEntityClass) : IEmbeddedEntityField<List<String>>(entityClass.id, key) {
    override fun defaultValue(): List<String> = emptyList()
    override fun autoAddNew(): Boolean = true
    override fun autoInitValue(): Boolean = true

    override fun getFieldKey(): String = key
    override fun addNew() {
        addNew(
                "cabin storage",
                "connection",
                emptyList(),
                true,
                null)
    }

    companion object {
        const val key = "cabin-storage"
    }

    override fun createFieldValue(entityId: String): EntityFieldValue<List<String>> =
            object : EntityFieldValue<List<String>>(entityId, id, { parse(it.value)!! }) {}
}