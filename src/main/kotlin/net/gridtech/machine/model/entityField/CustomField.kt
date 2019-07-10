package net.gridtech.machine.model.entityField

import net.gridtech.core.data.IField
import net.gridtech.core.util.generateId
import net.gridtech.machine.model.EntityFieldValue
import net.gridtech.machine.model.IBaseProperty
import net.gridtech.machine.model.IEntityField
import net.gridtech.machine.model.property.field.FieldDescription
import net.gridtech.machine.model.property.field.FieldDescriptionProperty
import net.gridtech.machine.model.property.field.ValueDescription

class CustomField(field: IField) : IEntityField<ValueDescription>(field.id) {
    val description = FieldDescriptionProperty(this)
    override fun getDescriptionProperty(): IBaseProperty<*, IField>? = description

    companion object {
        private val tags = listOf("custom field")
        fun create(field: IField): CustomField? =
                if (field.tags.containsAll(tags))
                    CustomField(field)
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
                        FieldDescription.empty()
                )
    }

    override fun createFieldValue(entityId: String): EntityFieldValue<ValueDescription> =
            object : EntityFieldValue<ValueDescription>(entityId, id, { fieldValue ->
                description.value?.valueDescriptions
                        ?.find { Regex(it.valueExp).matches(fieldValue.value) }
                        ?: ValueDescription.create(fieldValue)
            }) {}
}
