package net.gridtech.machine.model.entity

import net.gridtech.core.data.INode
import net.gridtech.machine.model.IEntity
import net.gridtech.machine.model.entityClass.MachineClass

class Machine : IEntity<MachineClass> {
    constructor(node: INode) : super(node)
    constructor(id: String, t: MachineClass) : super(id, t)

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
        val tags = listOf("machine")
        fun create(node: INode): Machine? =
                if (node.tags.containsAll(tags))
                    Machine(node)
                else
                    null
    }
}