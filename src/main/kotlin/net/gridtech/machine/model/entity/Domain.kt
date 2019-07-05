package net.gridtech.machine.model.entity

import net.gridtech.core.data.INode
import net.gridtech.machine.model.DataHolder
import net.gridtech.machine.model.IEntity

class Domain(node: INode) : IEntity(node) {
    companion object {
        fun create(node: INode): Domain? =
                if (node.id == DataHolder.instance.domainNodeId)
                    Domain(node)
                else
                    null
    }
}