package net.gridtech.machine.model.entityClass

import net.gridtech.core.data.INodeClass
import net.gridtech.core.util.generateId
import net.gridtech.machine.model.IEntityClass
import net.gridtech.machine.model.entity.Machine
import net.gridtech.machine.model.entityField.RunningStatusField
import net.gridtech.machine.model.entityField.SecretField


class MachineClass(id: String) : IEntityClass(id) {
    val runningStatus = RunningStatusField(this)
    val secret = SecretField(this)

    fun addNew(name: String, alias: String): INodeClass? =
            addNew(name, alias, tags, true)

    fun addNewEntity(name: String, alias: String, parentId: String) =
            addNewEntity(
                    generateId(),
                    parentId,
                    name,
                    alias,
                    Machine.tags,
                    emptyList(),
                    emptyList(),
                    null
            )

    companion object {
        val tags = listOf("machine class")
        fun create(nodeClass: INodeClass): MachineClass? =
                if (nodeClass.tags.containsAll(tags))
                    MachineClass(nodeClass.id).apply { initialize(nodeClass) }
                else
                    null

    }
}