package net.gridtech.machine.model.entityField

import net.gridtech.core.data.IField
import net.gridtech.core.util.cast
import net.gridtech.machine.model.EntityFieldValue
import net.gridtech.machine.model.IEntityField

class DeviceHealthField(field: IField) : IEntityField<Boolean>(field) {
    companion object {
        val key = "device-healthy"
        fun create(field: IField): DeviceHealthField? =
                if (field.matchKey(key))
                    DeviceHealthField(field)
                else
                    null

        fun add(entityClassId: String) = add(
                key = key,
                nodeClassId = entityClassId,
                name = "device healthy",
                alias = "isHealthy",
                tags = emptyList(),
                through = true,
                description = null
        )
    }

    override fun createFieldValue(entityId: String): EntityFieldValue<Boolean> =
            object : EntityFieldValue<Boolean>(entityId, source.id, { cast(it.value)!! }) {}
}