package net.gridtech.machine.model.property

import net.gridtech.core.data.IField
import net.gridtech.core.util.APIExceptionEnum
import net.gridtech.core.util.generateId
import net.gridtech.core.util.parse
import net.gridtech.machine.model.DataHolder
import net.gridtech.machine.model.IBaseProperty
import net.gridtech.machine.model.entityField.CustomField

class FieldDescriptionProperty(private val customField: CustomField)
    : IBaseProperty<FieldDescription, IField>({ parse(it.description) }, FieldDescription.empty()) {

    fun addValueDescription(valueDescription: ValueDescription) {
        valueDescription.id = generateId()
        value?.apply {
            val newValueDescriptions = valueDescriptions.toMutableList()
            newValueDescriptions.add(valueDescription)
            this.copy(valueDescriptions = newValueDescriptions)
            customField.updateDescription(this.copy(valueDescriptions = newValueDescriptions))
        }
    }

    fun updateValueDescription(valueDescription: ValueDescription) {
        value?.apply {
            if (valueDescriptions.find { it.id == valueDescription.id } != null) {
                customField.updateDescription(this.copy(
                        valueDescriptions =
                        valueDescriptions
                                .map { if (it.id == valueDescription.id) valueDescription else it }))
            }
        }
    }

    fun deleteValueDescription(id: String) {
        APIExceptionEnum.ERR10_CAN_NOT_BE_DELETED.assert(DataHolder.instance.checkDependency(id))
        value?.apply {
            if (valueDescriptions.find { it.id == id } != null) {
                customField.updateDescription(this.copy(
                        valueDescriptions =
                        valueDescriptions
                                .filter { it.id != id }))
            }
        }
    }
}

data class FieldDescription(
        var valueDescriptions: List<ValueDescription>
) {
    companion object {
        fun empty() = FieldDescription(emptyList())
    }
}

data class ValueDescription(
        var id: String,
        var name: String,
        var alias: String,
        var valueExp: String,
        var extra: String,
        var color: String
) {
    companion object {
        fun empty() = ValueDescription(
                id = "",
                name = "",
                alias = "",
                valueExp = "",
                extra = "",
                color = "#ffffff"
        )
    }
}