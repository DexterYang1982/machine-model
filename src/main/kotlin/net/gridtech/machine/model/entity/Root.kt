package net.gridtech.machine.model.entity

import net.gridtech.core.data.INode
import net.gridtech.core.util.ID_NODE_ROOT
import net.gridtech.machine.model.IEntity
import net.gridtech.machine.model.entityClass.RootClass

class Root(node: INode) : IEntity<RootClass>(node) {
    companion object {
        fun create(node: INode): Root? =
                if (node.id == ID_NODE_ROOT)
                    Root(node)
                else
                    null
    }
}