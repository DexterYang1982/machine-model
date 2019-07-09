package net.gridtech.machine.model.entityField

import net.gridtech.core.data.IField
import net.gridtech.machine.model.EntityFieldValue
import net.gridtech.machine.model.IEntityField

class ModbusSlaveConnectionField(field: IField) : IEntityField<Boolean>(field) {
    companion object {
        val key = "slave-connection"
        fun create(field: IField): SecretField? =
                if (field.matchKey(key))
                    SecretField(field)
                else
                    null

        fun add(entityClassId: String) = add(
                key = key,
                nodeClassId = entityClassId,
                name = "slave connection",
                alias = "connection",
                tags = emptyList(),
                through = true,
                description = null
        )
    }

    override fun createFieldValue(entityId: String): EntityFieldValue<Boolean> =
            object : EntityFieldValue<Boolean>(entityId, source.id, { fieldValue ->
                fieldValue.value == "true"
            }) {}
}