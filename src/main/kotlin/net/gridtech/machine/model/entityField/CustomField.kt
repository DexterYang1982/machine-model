package net.gridtech.machine.model.entityField

import net.gridtech.core.data.IField
import net.gridtech.core.util.generateId
import net.gridtech.machine.model.IEntityField
import net.gridtech.machine.model.property.ValueDescriptionProperty

class CustomField(field: IField) : IEntityField(field) {

    val valueDescriptionProperty = ValueDescriptionProperty(this)

    override fun getDescription(): Any? = valueDescriptionProperty.current ?: ValueDescriptionProperty.create()

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
                    ValueDescriptionProperty.create())
        }
    }
}
