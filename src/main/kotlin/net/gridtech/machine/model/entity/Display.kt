package net.gridtech.machine.model.entity

import net.gridtech.core.data.INode
import net.gridtech.core.util.generateId
import net.gridtech.machine.model.IEntity
import net.gridtech.machine.model.entityClass.DisplayClass


class Display(node: INode) : IEntity<DisplayClass>(node) {

    companion object {
        val tags = listOf("display")
        fun create(node: INode): Display? =
                if (node.tags.containsAll(tags))
                    Display(node)
                else
                    null

        fun add(displayClass: DisplayClass, parentId: String, name: String, alias: String) =
                add(
                        id = generateId(),
                        parentId = parentId,
                        nodeClassId = displayClass.id,
                        name = name,
                        alias = alias,
                        tags = tags,
                        externalNodeIdScope = emptyList(),
                        externalNodeClassTagScope = emptyList(),
                        description = null
                )
    }
}