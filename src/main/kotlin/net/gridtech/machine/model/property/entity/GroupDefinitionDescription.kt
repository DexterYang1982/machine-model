package net.gridtech.machine.model.property.entity

import net.gridtech.core.data.INode
import net.gridtech.core.util.parse
import net.gridtech.machine.model.IBaseProperty
import net.gridtech.machine.model.Trigger
import net.gridtech.machine.model.entity.Group

class GroupDefinitionDescription(private val group: Group)
    : IBaseProperty<GroupDefinition, INode>({ parse(it.description) }, GroupDefinition.empty())


data class GroupDefinition(
        var triggers: List<Trigger>
) {
    companion object {
        fun empty() = GroupDefinition(emptyList())
    }
}