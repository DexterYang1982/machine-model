package net.gridtech.machine.model.entityField

import net.gridtech.core.data.IField
import net.gridtech.core.util.KEY_FIELD_SECRET
import net.gridtech.machine.model.EntityFieldValue
import net.gridtech.machine.model.IEntityField


class SecretField(field: IField) : IEntityField<String>(field) {
    companion object {
        val key = KEY_FIELD_SECRET
        fun create(field: IField): SecretField? =
                if (field.matchKey(key))
                    SecretField(field)
                else
                    null
    }

    override fun createFieldValue(entityId:String): EntityFieldValue<String> =
            object : EntityFieldValue<String>(entityId, source.id, { it.value }) {}
}