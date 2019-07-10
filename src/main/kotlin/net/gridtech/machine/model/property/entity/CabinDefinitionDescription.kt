package net.gridtech.machine.model.property.entity

import net.gridtech.core.data.INode
import net.gridtech.core.util.parse
import net.gridtech.machine.model.IBaseProperty
import net.gridtech.machine.model.ReadCondition
import net.gridtech.machine.model.entity.Cabin

class CabinDefinitionDescription(private val cabin: Cabin)
    : IBaseProperty<CabinDefinition, INode>({ parse(it.description) }, CabinDefinition.empty())


data class CabinDefinition(
        var exportSingle: Boolean,
        var emptyCondition: ReadCondition
) {
    companion object {
        fun empty() = CabinDefinition(true, ReadCondition.empty())
    }
}