package net.gridtech.machine.model.entityClass

import net.gridtech.core.data.INodeClass
import net.gridtech.core.util.ID_NODE_CLASS_ROOT
import net.gridtech.machine.model.IEntityClass
import net.gridtech.machine.model.entityField.RunningStatusField
import net.gridtech.machine.model.entityField.SecretField

class RootClass(id: String) : IEntityClass(id) {
    val runningStatus = RunningStatusField(this)
    val secret = SecretField(this)

    companion object {
        fun create(nodeClass: INodeClass): RootClass? =
                if (nodeClass.id == ID_NODE_CLASS_ROOT)
                    RootClass(nodeClass.id).apply { initialize(nodeClass) }
                else
                    null
    }
}