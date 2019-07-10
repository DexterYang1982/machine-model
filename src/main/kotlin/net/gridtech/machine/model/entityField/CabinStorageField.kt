package net.gridtech.machine.model.entityField

import net.gridtech.core.util.cast
import net.gridtech.core.util.compose
import net.gridtech.machine.model.EntityFieldValue
import net.gridtech.machine.model.IEmbeddedEntityField
import net.gridtech.machine.model.IEntityClass


class CabinStorageField(entityClass: IEntityClass) : IEmbeddedEntityField<List<String>>(compose(entityClass.id, RunningStatusField.key)) {
    companion object {
        const val key = "cabin-storage"

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
            object : EntityFieldValue<List<String>>(entityId, id, { cast(it.value)!! }) {}
}