package net.gridtech.machine.model.entityField

import net.gridtech.core.data.IField
import net.gridtech.core.util.cast
import net.gridtech.machine.model.EntityFieldValue
import net.gridtech.machine.model.IEntityField


class CabinEmptyField(field: IField) : IEntityField<Boolean>(field) {
    companion object {
        val key = "cabin-is-empty"
        fun create(field: IField): CabinEmptyField? =
                if (field.matchKey(key))
                    CabinEmptyField(field)
                else
                    null

        fun add(entityClassId: String) = add(
                key = key,
                nodeClassId = entityClassId,
                name = "cabin is empty",
                alias = "isEmpty",
                tags = emptyList(),
                through = true,
                description = null
        )
    }

    override fun createFieldValue(entityId: String): EntityFieldValue<Boolean> =
            object : EntityFieldValue<Boolean>(entityId, source.id, { cast(it.value)!! }) {}
}