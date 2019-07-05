package net.gridtech.machine.model.entityClass

import net.gridtech.core.data.INodeClass
import net.gridtech.machine.model.DataHolder
import net.gridtech.machine.model.IEntityClass


class DomainClass(nodeClass: INodeClass) : IEntityClass(nodeClass) {
    companion object {
        fun create(nodeClass: INodeClass): DomainClass? =
                if (nodeClass.id == DataHolder.instance.domainNodeClassId)
                    DomainClass(nodeClass)
                else
                    null
    }
}