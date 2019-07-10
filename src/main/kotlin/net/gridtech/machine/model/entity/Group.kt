package net.gridtech.machine.model.entity

import net.gridtech.core.data.INode
import net.gridtech.core.util.generateId
import net.gridtech.machine.model.IEntity
import net.gridtech.machine.model.entityClass.GroupClass


class Group(node: INode) : IEntity<GroupClass>(node) {

    companion object {
        val tags = listOf("group")
        fun create(node: INode): Group? =
                if (node.tags.containsAll(tags))
                    Group(node)
                else
                    null

        fun add(groupClass: GroupClass, parentId: String, name: String, alias: String) =
                add(
                        id = generateId(),
                        parentId = parentId,
                        nodeClassId = groupClass.id,
                        name = name,
                        alias = alias,
                        tags = tags,
                        externalNodeIdScope = emptyList(),
                        externalNodeClassTagScope = emptyList(),
                        description = null
                )
    }
}