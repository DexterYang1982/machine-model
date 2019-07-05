package net.gridtech.machine.model.entityClass

import net.gridtech.core.data.INodeClass
import net.gridtech.core.util.ID_NODE_CLASS_ROOT
import net.gridtech.machine.model.IEntityClass

class RootClass(nodeClass: INodeClass) : IEntityClass(nodeClass) {
    companion object {
        fun create(nodeClass: INodeClass): RootClass? =
                if (nodeClass.id == ID_NODE_CLASS_ROOT)
                    RootClass(nodeClass)
                else
                    null
    }
}