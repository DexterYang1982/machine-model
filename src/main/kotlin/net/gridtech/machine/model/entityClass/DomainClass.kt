package net.gridtech.machine.model.entityClass

import net.gridtech.core.data.INodeClass
import net.gridtech.machine.model.DataHolder
import net.gridtech.machine.model.IEntityClass
import net.gridtech.machine.model.entityField.RunningStatusField


class DomainClass(id: String) : IEntityClass(id) {
    val runningStatus = RunningStatusField(this)

    companion object {
        fun create(nodeClass: INodeClass): DomainClass? =
                if (nodeClass.id == DataHolder.instance.domainNodeClassId)
                    DomainClass(nodeClass.id).apply { initialize(nodeClass) }
                else
                    null
    }
}