package net.gridtech.machine.model.entity

import net.gridtech.core.data.INode
import net.gridtech.core.util.generateId
import net.gridtech.machine.model.IEntity
import net.gridtech.machine.model.entityClass.MachineClass

class Machine(node: INode) : IEntity(node) {
    companion object {
        private val tags = listOf("machine")
        fun create(node: INode): Machine? =
                if (node.tags.containsAll(tags))
                    Machine(node)
                else
                    null

        fun add(machineClass: MachineClass,parentId:String, name: String, alias: String) =
                add(
                        id = generateId(),
                        parentId = parentId,
                        nodeClassId = machineClass.data.id,
                        name = name,
                        alias = alias,
                        tags = tags,
                        externalNodeIdScope = emptyList(),
                        externalNodeClassTagScope = emptyList(),
                        description = null
                )
    }
}