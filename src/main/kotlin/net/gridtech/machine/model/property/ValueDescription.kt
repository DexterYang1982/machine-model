package net.gridtech.machine.model.property

import net.gridtech.core.data.IField
import net.gridtech.core.util.APIExceptionEnum
import net.gridtech.core.util.generateId
import net.gridtech.core.util.parse
import net.gridtech.machine.model.DataHolder
import net.gridtech.machine.model.IProperty
import net.gridtech.machine.model.entityField.CustomField

class ValueDescriptionProperty(private val customField: CustomField) : IProperty<List<ValueDescription>, IField>(
        customField.updatePublisher,
        customField.deletePublisher,
        {
            parse(it.description)
        }
) {
    companion object {
        fun create() = emptyList<ValueDescription>()
    }

    fun addValueDescription(valueDescription: ValueDescription) {
        valueDescription.id = generateId()
        val newValueDescriptions = current?.toMutableList()
        newValueDescriptions?.add(valueDescription)
        newValueDescriptions?.apply {
            customField.updateDescription(this)
        }
    }

    fun updateValueDescription(valueDescription: ValueDescription) {
        current?.map {
            if (it.id == valueDescription.id)
                valueDescription
            else
                it
        }?.apply {
            customField.updateDescription(this)
        }
    }

    fun deleteValueDescription(id: String) {
        APIExceptionEnum.ERR10_CAN_NOT_BE_DELETED.assert(DataHolder.instance.checkDependency(id))
        current?.filter { it.id != id }?.apply {
            customField.updateDescription(this)
        }
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