package net.gridtech.machine.model.entity

import net.gridtech.core.data.INode
import net.gridtech.core.util.generateId
import net.gridtech.machine.model.IEntity
import net.gridtech.machine.model.entityClass.DisplayClass
import net.gridtech.machine.model.entityField.RunningStatusField


class Display(node: INode) : IEntity(node) {

    val displayClientVersion = getEntityClass<DisplayClass>().displayClientVersionDescription

    val runningStatus
        get() = getEntityField<RunningStatusField>(source.nodeClassId, RunningStatusField.key).getFieldValue(source.id)

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
                        nodeClassId = displayClass.source.id,
                        name = name,
                        alias = alias,
                        tags = tags,
                        externalNodeIdScope = emptyList(),
                        externalNodeClassTagScope = emptyList(),
                        description = null
                )
    }
}