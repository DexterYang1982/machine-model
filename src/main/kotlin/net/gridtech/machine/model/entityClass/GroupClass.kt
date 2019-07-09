package net.gridtech.machine.model.entityClass

import net.gridtech.core.data.INodeClass
import net.gridtech.machine.model.IEntityClass

class GroupClass(nodeClass: INodeClass) : IEntityClass(nodeClass) {
    companion object {
        val tags = listOf("group class")
        fun create(nodeClass: INodeClass): GroupClass? =
                if (nodeClass.tags.containsAll(tags))
                    GroupClass(nodeClass)
                else
                    null

        fun add(name: String, alias: String) =
                add(name, alias, false, tags, null)
    }
}