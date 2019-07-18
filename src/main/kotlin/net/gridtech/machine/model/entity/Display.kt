package net.gridtech.machine.model.entity

import net.gridtech.core.data.INode
import net.gridtech.core.util.cast
import net.gridtech.machine.model.DataHolder
import net.gridtech.machine.model.IEntity
import net.gridtech.machine.model.entityClass.DisplayClass


class Display(id: String, entityClass: DisplayClass) : IEntity<DisplayClass>(id, entityClass) {

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
                    Display(node.id, cast(DataHolder.instance.entityClassHolder[node.nodeClassId])!!).apply { initialize(node) }
                else
                    null

    }
}