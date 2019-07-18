package net.gridtech.machine.model.entity

import net.gridtech.core.data.INode
import net.gridtech.core.util.cast
import net.gridtech.machine.model.DataHolder
import net.gridtech.machine.model.IEntity
import net.gridtech.machine.model.entityClass.TunnelClass
import net.gridtech.machine.model.property.entity.TunnelDefinitionDescription


class Tunnel(id: String, entityClass: TunnelClass) : IEntity<TunnelClass>(id, entityClass) {
    override val description = TunnelDefinitionDescription(this)

    fun addNew(parentId: String, name: String, alias: String) =
            addNew(
                    parentId,
                    name,
                    alias,
                    tags,
                    emptyList(),
                    emptyList()
            )

    companion object {
        val tags = listOf("tunnel")
        fun create(node: INode): Tunnel? =
                if (node.tags.containsAll(tags))
                    Tunnel(node.id, cast(DataHolder.instance.entityClassHolder[node.nodeClassId])!!).apply { initialize(node) }
                else
                    null
    }
}