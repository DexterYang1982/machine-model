package net.gridtech.machine.model.entityField

import net.gridtech.core.data.IField
import net.gridtech.core.util.generateId
import net.gridtech.core.util.parse
import net.gridtech.machine.model.EntityFieldValue
import net.gridtech.machine.model.IBaseProperty
import net.gridtech.machine.model.IEntityField
import net.gridtech.machine.model.property.field.Trigger
import net.gridtech.machine.model.property.field.TriggerFieldDescription


class TriggerField(field: IField) : IEntityField<TriggerRecord>(field.id) {
    val description = TriggerFieldDescription(this)
    override fun getDescriptionProperty(): IBaseProperty<*, IField>? = description

    companion object {
        private val tags = listOf("trigger field")
        fun create(field: IField): TriggerField? =
                if (field.tags.containsAll(tags))
                    TriggerField(field)
                else
                    null

        fun addNew(entityClassId: String, name: String, alias: String): IField? =
                addNew(
                        "trigger-${generateId()}",
                        entityClassId,
                        name,
                        alias,
                        tags,
                        true,
                        Trigger.empty()
                )
    }

    override fun createFieldValue(entityId: String): EntityFieldValue<TriggerRecord> =
            object : EntityFieldValue<TriggerRecord>(entityId, id, { fieldValue ->
                parse(fieldValue.value)
            }) {}
}

data class TriggerRecord(
        var triggerTime: Long
) {
    companion object {
        fun empty() = TriggerRecord(-1)
    }
}