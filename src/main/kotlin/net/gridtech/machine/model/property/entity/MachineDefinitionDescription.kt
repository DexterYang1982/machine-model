package net.gridtech.machine.model.property.entity

import net.gridtech.core.data.INode
import net.gridtech.core.util.parse
import net.gridtech.machine.model.IBaseProperty
import net.gridtech.machine.model.Trigger
import net.gridtech.machine.model.entity.Machine

class MachineDefinitionDescription(private val machine: Machine)
    : IBaseProperty<MachineDefinition, INode>({ parse(it.description) }, MachineDefinition.empty())


data class MachineDefinition(
        var triggers: List<Trigger>
) {
    companion object {
        fun empty() = MachineDefinition(emptyList())
    }
}