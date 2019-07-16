package net.gridtech.machine.model.entityField

import net.gridtech.core.data.IField
import net.gridtech.core.util.generateId
import net.gridtech.machine.model.EntityFieldValue
import net.gridtech.machine.model.IBaseProperty
import net.gridtech.machine.model.IEntityField
import net.gridtech.machine.model.property.field.CustomFieldDescription
import net.gridtech.machine.model.property.field.FieldValueDescription
import net.gridtech.machine.model.property.field.ValueDescription

class CustomField(field: IField) : IEntityField<ValueDescription>(field.id) {
    val description = CustomFieldDescription(this)
    override fun getDescriptionProperty(): IBaseProperty<*, IField>? = description

    companion object {
        private val tags = listOf("custom field")
        fun create(field: IField): CustomField? =
                if (field.tags.containsAll(tags))
                    CustomField(field).apply { initialize(field) }
                else
                    null

        fun addNew(entityClassId: String, name: String, alias: String): IField? =
                addNew(
                        "custom-${generateId()}",
                        entityClassId,
                        name,
                        alias,
                        tags,
                        true,
                        FieldValueDescription.empty()
                )
    }

    override fun createFieldValue(entityId: String): EntityFieldValue<ValueDescription> =
            object : EntityFieldValue<ValueDescription>(entityId, id, { fieldValue ->
                description.value?.valueDescriptions
                        ?.find { Regex(it.valueExp).matches(fieldValue.value) }
                        ?: ValueDescription.create(fieldValue)
            }) {}
}
