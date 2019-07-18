package net.gridtech.machine.model.entity

import net.gridtech.core.data.INode
import net.gridtech.core.util.cast
import net.gridtech.machine.model.DataHolder
import net.gridtech.machine.model.IEntity
import net.gridtech.machine.model.entityClass.DomainClass

class Domain(id: String, entityClass: DomainClass) : IEntity<DomainClass>(id, entityClass) {
    companion object {
        val tags = listOf("domain")
        fun create(node: INode): Domain? =
                if (node.tags.containsAll(tags))
                    Domain(node.id, cast(DataHolder.instance.entityClassHolder[node.nodeClassId])!!).apply { initialize(node) }
                else
                    null
    }
}