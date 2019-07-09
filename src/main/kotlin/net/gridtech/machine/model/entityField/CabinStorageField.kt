package net.gridtech.machine.model.entityField

import net.gridtech.core.data.IField
import net.gridtech.core.util.cast
import net.gridtech.machine.model.EntityFieldValue
import net.gridtech.machine.model.IEntityField


class CabinStorageField(field: IField) : IEntityField<List<String>>(field) {
    companion object {
        val key = "cabin-storage"
        fun create(field: IField): CabinStorageField? =
                if (field.matchKey(key))
                    CabinStorageField(field)
                else
                    null

        fun add(entityClassId: String) = add(
                key = key,
                nodeClassId = entityClassId,
                name = "cabin storage",
                alias = "connection",
                tags = emptyList(),
                through = true,
                description = null
        )
    }

    override fun createFieldValue(entityId: String): EntityFieldValue<List<String>> =
            object : EntityFieldValue<List<String>>(entityId, source.id, { cast(it.value)!! }) {}
}