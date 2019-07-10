package net.gridtech.machine.model.entityClass

import net.gridtech.core.data.INodeClass
import net.gridtech.machine.model.IEntityClass

class TunnelClass(id: String) : IEntityClass(id) {

    fun addNew(name: String, alias: String) =
            addNew(name, alias, tags, false)

    companion object {
        val tags = listOf("tunnel class")
        fun create(nodeClass: INodeClass): TunnelClass? =
                if (nodeClass.tags.containsAll(tags))
                    TunnelClass(nodeClass.id).apply { initialize(nodeClass) }
                else
                    null
    }
}