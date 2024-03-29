package net.gridtech.machine.model.entity

import net.gridtech.core.data.INode
import net.gridtech.core.util.ID_NODE_ROOT
import net.gridtech.core.util.cast
import net.gridtech.machine.model.DataHolder
import net.gridtech.machine.model.IEntity
import net.gridtech.machine.model.entityClass.RootClass

class Root(id: String, entityClass: RootClass) : IEntity<RootClass>(id, entityClass) {
    companion object {
        fun create(node: INode): Root? =
                if (node.id == ID_NODE_ROOT)
                    Root(node.id, cast(DataHolder.instance.entityClassHolder[node.nodeClassId])!!).apply { initialize(node) }
                else
                    null
    }
}