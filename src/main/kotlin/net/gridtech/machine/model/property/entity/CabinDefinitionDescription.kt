package net.gridtech.machine.model.property.entity

import net.gridtech.core.data.INode
import net.gridtech.core.util.parse
import net.gridtech.machine.model.IBaseProperty
import net.gridtech.machine.model.entity.Cabin

class CabinDefinitionDescription(private val cabin: Cabin)
    : IBaseProperty<CabinDefinition, INode>({ parse(it.description) }, CabinDefinition.empty())


data class CabinDefinition(
        var exportSingle: Boolean,
        var emptyTrigger: DeviceStatusValue? = null
) {
    companion object {
        fun empty() = CabinDefinition(true)
    }
}