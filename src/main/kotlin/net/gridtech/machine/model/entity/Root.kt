package net.gridtech.machine.model.entity

import net.gridtech.core.data.INode
import net.gridtech.core.util.ID_NODE_ROOT
import net.gridtech.machine.model.IEntity
import net.gridtech.machine.model.entityField.RunningStatusField

class Root(node: INode) : IEntity(node) {
    val runningStatus
        get() = getEntityField<RunningStatusField>(source.nodeClassId, RunningStatusField.key).getFieldValue(source.id)
    companion object {
        fun create(node: INode): Root? =
                if (node.id == ID_NODE_ROOT)
                    Root(node)
                else
                    null
    }
}