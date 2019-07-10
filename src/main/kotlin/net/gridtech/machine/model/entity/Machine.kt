package net.gridtech.machine.model.entity

import net.gridtech.core.data.INode
import net.gridtech.machine.model.IEntity
import net.gridtech.machine.model.entityClass.MachineClass

class Machine(node: INode) : IEntity<MachineClass>(node) {
    companion object {
        val tags = listOf("machine")
        fun create(node: INode): Machine? =
                if (node.tags.containsAll(tags))
                    Machine(node)
                else
                    null
    }
}