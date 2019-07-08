package net.gridtech.machine.model.entityField

import net.gridtech.core.data.IField
import net.gridtech.core.util.generateId
import net.gridtech.machine.model.EntityFieldValue
import net.gridtech.machine.model.IBaseProperty
import net.gridtech.machine.model.IEntityField
import net.gridtech.machine.model.property.FieldDescription
import net.gridtech.machine.model.property.FieldDescriptionProperty
import net.gridtech.machine.model.property.ValueDescription

class CustomField(field: IField) : IEntityField<ValueDescription>(field) {
    val valueDescriptionProperty = FieldDescriptionProperty(this)
    override fun getDescriptionProperty(): IBaseProperty<*, IField>? = valueDescriptionProperty

    companion object {
        private val tags = listOf("custom field")
        fun create(field: IField): CustomField? =
                if (field.tags.containsAll(tags))
                    CustomField(field)
                else
                    null

        fun add(entityClassId: String, name: String, alias: String) {
            add(
                    "CUSTOM-${generateId()}",
                    entityClassId,
                    name,
                    alias,
                    tags,
                    true,
                    FieldDescription.empty())
        }
    }

    override fun createFieldValue(entityId: String): EntityFieldValue<ValueDescription> =
            object : EntityFieldValue<ValueDescription>(entityId, source.id, { fieldValue ->
                valueDescriptionProperty.value?.valueDescriptions
                        ?.find { Regex(it.valueExp).matches(fieldValue.value) }
                        ?: ValueDescription.create(fieldValue)
            }) {}
}
