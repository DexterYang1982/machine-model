package net.gridtech.machine.model.entity

import net.gridtech.core.data.INode
import net.gridtech.machine.model.IEntity
import net.gridtech.machine.model.entityClass.DisplayClass


class Display : IEntity<DisplayClass> {
    constructor(node: INode) : super(node)
    constructor(id: String, t: DisplayClass) : super(id, t)

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
        val tags = listOf("display")
        fun create(node: INode): Display? =
                if (node.tags.containsAll(tags))
                    Display(node)
                else
                    null

    }
}