package net.gridtech.machine.model.entity

import net.gridtech.core.data.INode
import net.gridtech.machine.model.IEntity
import net.gridtech.machine.model.entityClass.DomainClass

class Domain(node: INode) : IEntity<DomainClass>(node) {
    companion object {
        val tags = listOf("domain")
        fun create(node: INode): Domain? =
                if (node.tags.containsAll(tags))
                    Domain(node)
                else
                    null
    }
}