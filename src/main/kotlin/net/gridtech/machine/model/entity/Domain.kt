package net.gridtech.machine.model.entity

import net.gridtech.core.data.INode
import net.gridtech.machine.model.DataHolder
import net.gridtech.machine.model.IEntity
import net.gridtech.machine.model.entityClass.DomainClass

class Domain(node: INode) : IEntity<DomainClass>(node) {
    companion object {
        fun create(node: INode): Domain? =
                if (node.id == DataHolder.instance.domainNodeId)
                    Domain(node)
                else
                    null
    }
}