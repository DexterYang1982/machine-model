package net.gridtech.machine.model.entity

import net.gridtech.core.data.INode
import net.gridtech.core.util.cast
import net.gridtech.machine.model.DataHolder
import net.gridtech.machine.model.IEntity
import net.gridtech.machine.model.entityClass.TunnelClass
import net.gridtech.machine.model.entityField.ProcessRuntime
import net.gridtech.machine.model.entityField.ProcessState
import net.gridtech.machine.model.property.entity.TunnelDefinitionDescription


class Tunnel(id: String, entityClass: TunnelClass) : IEntity<TunnelClass>(id, entityClass) {
    override val description = TunnelDefinitionDescription(this)

    fun addNew(parentId: String, name: String, alias: String) =
            addNew(
                    parentId,
                    name,
                    alias,
                    tags,
                    emptyList(),
                    emptyList()
            )

    companion object {
        val tags = listOf("tunnel")
        fun create(node: INode): Tunnel? =
                if (node.tags.containsAll(tags))
                    Tunnel(node.id, cast(DataHolder.instance.entityClassHolder[node.nodeClassId])!!).apply { initialize(node) }
                else
                    null
    }

    fun updateTunnelProcessState(processRuntime: ProcessRuntime) {
        val currentTransactionField = entityClass.currentTransaction.getFieldValue(this)
        currentTransactionField.value?.apply {
            val currentProcess = this.transactionProcesses.find {
                it.transactionSession == processRuntime.transactionSession &&
                        it.transactionPhaseSession == processRuntime.transactionPhaseSession &&
                        it.state != processRuntime.state
            }
            if (currentProcess != null) {
                if (processRuntime.state == ProcessState.FINISHED) {
                    val transaction = description.value?.transactions?.find { it.id == processRuntime.transactionId }
                    val transactionPhase = transaction?.phases?.find { it.id == processRuntime.transactionPhaseId }
                    transactionPhase?.exportCabinId?.let { exportCabinId ->
                        DataHolder.instance.getEntityByIdObservable<Cabin>(exportCabinId).subscribe { exportCabin, _ ->
                            val products = exportCabin.export(processRuntime.session())
                            if (products.isNotEmpty()) {
                                if (transaction.targetCabinId == exportCabin.id) {
                                    finishedExportation = true
                                }
                                transactionPhase.importCabinId?.let { importCabinId ->
                                    DataHolder.instance.getEntityByIdObservable<Cabin>(importCabinId).subscribe { importCabin, _ ->
                                        importCabin.import(products, processRuntime.session())
                                    }
                                }
                            }
                        }
                    }
                }
                currentTransactionField.update(this.transactionProcesses
                        .map { if (it == currentProcess) processRuntime else it }
                        , processRuntime.session())
            }
        }
    }
}