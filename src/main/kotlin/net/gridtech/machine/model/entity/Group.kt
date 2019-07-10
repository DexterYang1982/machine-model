package net.gridtech.machine.model.entity

import net.gridtech.core.data.INode
import net.gridtech.machine.model.IEntity
import net.gridtech.machine.model.entityClass.GroupClass


class Group : IEntity<GroupClass> {
    constructor(node: INode) : super(node)
    constructor(id: String, t: GroupClass) : super(id, t)

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
                    Group(node)
                else
                    null
    }
}