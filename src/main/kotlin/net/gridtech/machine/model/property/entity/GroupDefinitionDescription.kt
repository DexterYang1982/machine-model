package net.gridtech.machine.model.property.entity

import net.gridtech.core.data.INode
import net.gridtech.core.util.APIExceptionEnum
import net.gridtech.core.util.generateId
import net.gridtech.core.util.parse
import net.gridtech.machine.model.DataHolder
import net.gridtech.machine.model.IBaseProperty
import net.gridtech.machine.model.Trigger
import net.gridtech.machine.model.entity.Group

class GroupDefinitionDescription(private val group: Group)
    : IBaseProperty<GroupDefinition, INode>({ parse(it.description) }, GroupDefinition.empty()) {
    fun addTrigger(trigger: Trigger) =
            value?.let {
                trigger.id = generateId()
                val newTriggers = it.triggers.toMutableList()
                newTriggers.add(trigger)
                group.updateDescription(it.copy(triggers = newTriggers))
            }

    fun updateTrigger(trigger: Trigger) =
            value?.takeIf { it.triggers.find { t -> t.id == trigger.id } != null }
                    ?.let {
                        group.updateDescription(it.copy(triggers = it.triggers.map { t -> if (t.id == trigger.id) trigger else t }))
                    }

    fun deleteTrigger(id: String) =
            APIExceptionEnum.ERR10_CAN_NOT_BE_DELETED.assert(DataHolder.instance.checkDependency(id)).let {
                value?.takeIf { it.triggers.find { t -> t.id == id } != null }
                        ?.let {
                            group.updateDescription(it.copy(triggers = it.triggers.filter { t -> t.id != id }))
                        }
            }
}


data class GroupDefinition(
        var triggers: List<Trigger>
) {
    companion object {
        fun empty() = GroupDefinition(emptyList())
    }
}