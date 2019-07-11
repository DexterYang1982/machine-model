package net.gridtech.machine.model.entityClass

import net.gridtech.core.data.INodeClass
import net.gridtech.machine.model.IEntityClass
import net.gridtech.machine.model.entityField.RunningStatusField
import net.gridtech.machine.model.entityField.SecretField


class DomainClass(id: String) : IEntityClass(id) {
    val runningStatus = RunningStatusField(this)
    val secret = SecretField(this)

    companion object {
        val tags = listOf("domain class")
        fun create(nodeClass: INodeClass): DomainClass? =
                if (nodeClass.tags.containsAll(tags))
                    DomainClass(nodeClass.id).apply { initialize(nodeClass) }
                else
                    null
    }
}