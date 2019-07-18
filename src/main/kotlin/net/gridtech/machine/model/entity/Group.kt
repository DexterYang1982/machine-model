package net.gridtech.machine.model.entity

import net.gridtech.core.data.INode
import net.gridtech.core.util.cast
import net.gridtech.machine.model.DataHolder
import net.gridtech.machine.model.IEntity
import net.gridtech.machine.model.entityClass.GroupClass
import net.gridtech.machine.model.property.entity.GroupDefinitionDescription


class Group(id: String, entityClass: GroupClass) : IEntity<GroupClass>(id, entityClass) {
    override val description = GroupDefinitionDescription(this)

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
        val tags = listOf("group")
        fun create(node: INode): Group? =
                if (node.tags.containsAll(tags))
                    Group(node.id, cast(DataHolder.instance.entityClassHolder[node.nodeClassId])!!).apply { initialize(node) }
                else
                    null
    }
}