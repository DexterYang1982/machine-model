package net.gridtech.machine.model.entity

import net.gridtech.core.data.INode
import net.gridtech.machine.model.DataHolder
import net.gridtech.machine.model.IEntity
import net.gridtech.machine.model.entityField.RunningStatusField

class Domain(node: INode) : IEntity(node) {
    val runningStatus
        get() = getEntityField<RunningStatusField>(source.nodeClassId, RunningStatusField.key).getFieldValue(source.id)

    companion object {
        fun create(node: INode): Domain? =
                if (node.id == DataHolder.instance.domainNodeId)
                    Domain(node)
                else
                    null
    }
}