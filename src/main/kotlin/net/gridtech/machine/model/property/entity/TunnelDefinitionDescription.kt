package net.gridtech.machine.model.property.entity

import net.gridtech.core.data.INode
import net.gridtech.core.util.APIExceptionEnum
import net.gridtech.core.util.generateId
import net.gridtech.core.util.parse
import net.gridtech.machine.model.DataHolder
import net.gridtech.machine.model.IBaseProperty
import net.gridtech.machine.model.entity.Tunnel

class TunnelDefinitionDescription(private val tunnel: Tunnel)
    : IBaseProperty<TunnelDefinition, INode>({ parse(it.description) }, TunnelDefinition.empty()) {
    fun updateMainCabin(mainCabinId: String) =
            value?.let {
                tunnel.updateDescription(it.copy(mainCabinId = mainCabinId))
            }

    fun addTransaction(transaction: TunnelTransaction) =
            value?.let {
                transaction.id = generateId()
                val transactions = it.transactions.toMutableList()
                transactions.add(transaction)
                tunnel.updateDescription(it.copy(transactions = transactions))
            }

    fun updateTransaction(transaction: TunnelTransaction) =
            value?.let {
                tunnel.updateDescription(it.copy(transactions = it.transactions.map { t -> if (t.id == transaction.id) transaction else t }))
            }

    fun deleteTransaction(id: String) =
            APIExceptionEnum.ERR10_CAN_NOT_BE_DELETED.assert(DataHolder.instance.checkDependency(id)).let {
                value?.takeIf { it.transactions.find { t -> t.id == id } != null }
                        ?.let {
                            tunnel.updateDescription(it.copy(transactions = it.transactions.filter { t -> t.id != id }))
                        }
            }


    fun addTransactionPhase(transactionId: String, phase: TunnelTransactionPhase) =
            value?.let {
                val transaction = it.transactions.find { t -> t.id == transactionId }
                transaction?.let { t ->
                    val phases = t.phases.toMutableList()
                    phase.id = generateId()
                    phases.add(phase)
                    val newTransaction = transaction.copy(phases = phases)
                    tunnel.updateDescription(it.copy(transactions = it.transactions.map { tt ->
                        if (tt.id == transactionId)
                            newTransaction
                        else
                            tt
                    }))
                }
            }

    fun updateTransactionPhase(transactionId: String, phase: TunnelTransactionPhase) =
            value?.let {
                val transaction = it.transactions.find { t -> t.id == transactionId }
                transaction?.let { t ->
                    val phases = t.phases.map { ttp ->
                        if (ttp.id == phase.id)
                            phase
                        else
                            ttp
                    }
                    val newTransaction = transaction.copy(phases = phases)
                    tunnel.updateDescription(it.copy(transactions = it.transactions.map { tt ->
                        if (tt.id == transactionId)
                            newTransaction
                        else
                            tt
                    }))
                }
            }

    fun deleteTransactionPhase(transactionId: String, phaseId: String) =
            APIExceptionEnum.ERR10_CAN_NOT_BE_DELETED.assert(DataHolder.instance.checkDependency(phaseId)).let {
                value?.let {
                    val transaction = it.transactions.find { t -> t.id == transactionId }
                    transaction?.let { t ->
                        val phases = t.phases.filter { ttp -> ttp.id != phaseId }
                        val newTransaction = transaction.copy(phases = phases)
                        tunnel.updateDescription(it.copy(transactions = it.transactions.map { tt ->
                            if (tt.id == transactionId)
                                newTransaction
                            else
                                tt
                        }))
                    }
                }
            }
}

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