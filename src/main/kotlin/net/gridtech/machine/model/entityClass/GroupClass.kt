package net.gridtech.machine.model.entityClass

import net.gridtech.core.data.INodeClass
import net.gridtech.machine.model.IEntityClass

class GroupClass(id: String) : IEntityClass(id) {
    fun addNew(name: String, alias: String) {
        addNew(name, alias, tags, false)
    }

    companion object {
        val tags = listOf("group class")
        fun create(nodeClass: INodeClass): GroupClass? =
                if (nodeClass.tags.containsAll(tags))
                    GroupClass(nodeClass.id).apply { initialize(nodeClass) }
                else
                    null
    }
}