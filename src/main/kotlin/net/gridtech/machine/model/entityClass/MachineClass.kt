package net.gridtech.machine.model.entityClass

import net.gridtech.core.data.INodeClass
import net.gridtech.machine.model.IEntityClass
import net.gridtech.machine.model.entityField.RunningStatusField
import net.gridtech.machine.model.entityField.SecretField


class MachineClass(id: String) : IEntityClass(id) {
    val runningStatus = RunningStatusField(this)
    val secret = SecretField(this)

    fun addNew(name: String, alias: String): INodeClass? =
            addNew(name, alias, tags, true)


    companion object {
        val tags = listOf("machine class")
        fun create(nodeClass: INodeClass): MachineClass? =
                if (nodeClass.tags.containsAll(tags))
                    MachineClass(nodeClass.id).apply { initialize(nodeClass) }
                else
                    null

    }
}