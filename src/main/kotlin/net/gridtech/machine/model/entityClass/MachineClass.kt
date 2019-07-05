package net.gridtech.machine.model.entityClass

import net.gridtech.core.data.INodeClass
import net.gridtech.machine.model.IEntityClass


class MachineClass(nodeClass: INodeClass) : IEntityClass(nodeClass) {
    companion object {
        private val tags = listOf("machine")
        fun create(nodeClass: INodeClass): MachineClass? =
                if (nodeClass.tags.containsAll(tags))
                    MachineClass(nodeClass)
                else
                    null

        fun add(name: String, alias: String) =
            add(name, alias, true, tags, null)
    }
}