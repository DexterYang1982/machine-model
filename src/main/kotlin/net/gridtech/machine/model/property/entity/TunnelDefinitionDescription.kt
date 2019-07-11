package net.gridtech.machine.model.property.entity

import net.gridtech.core.data.INode
import net.gridtech.core.util.parse
import net.gridtech.machine.model.IBaseProperty
import net.gridtech.machine.model.entity.Tunnel

class TunnelDefinitionDescription(private val tunnel: Tunnel)
    : IBaseProperty<TunnelDefinition, INode>({ parse(it.description) }, TunnelDefinition.empty())

data class TunnelDefinition(
        var mainCabinId: String,
        var transactions: List<TunnelTransaction>
) {
    companion object {
        fun empty() = TunnelDefinition("", emptyList())
    }
}

data class TunnelTransaction(
        var id: String,
        var name: String,
        var targetCabinId: String?,
        var phases: List<TunnelTransactionPhase>
)

data class TunnelTransactionPhase(
        var id: String,
        var delay: Long,
        var deviceId: String,
        var deviceProcessId: String,
        var exportCabinId: String?,
        var importCabinId: String?
)