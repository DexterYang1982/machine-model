package net.gridtech.machine.model.entityField

import net.gridtech.core.data.IField
import net.gridtech.machine.model.IEntityField

class SlaveConnectionField(field: IField) : IEntityField(field) {
    companion object {
        val key = "connection"
        fun create(field: IField): SecretField? =
                if (field.match(key))
                    SecretField(field)
                else
                    null

        fun add(entityClassId: String) = IEntityField.add(
                key = key,
                nodeClassId = entityClassId,
                name = "slave connection",
                alias = "connection",
                tags = emptyList(),
                through = true,
                description = null
        )
    }
}