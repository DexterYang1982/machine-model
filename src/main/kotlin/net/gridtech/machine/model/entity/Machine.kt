package net.gridtech.machine.model.entity

import net.gridtech.core.data.INode
import net.gridtech.core.util.cast
import net.gridtech.machine.model.DataHolder
import net.gridtech.machine.model.IBaseProperty
import net.gridtech.machine.model.IEntity
import net.gridtech.machine.model.entityClass.GroupClass
import net.gridtech.machine.model.entityClass.MachineClass
import net.gridtech.machine.model.property.entity.MachineDefinitionDescription

class Machine(id: String, entityClass: MachineClass) : IEntity<MachineClass>(id, entityClass) {
    override val description = MachineDefinitionDescription(this)

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
                    Machine(node.id, cast(DataHolder.instance.entityClassHolder[node.nodeClassId])!!).apply { initialize(node) }
                else
                    null
    }
}