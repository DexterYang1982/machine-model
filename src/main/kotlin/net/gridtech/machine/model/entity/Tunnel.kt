package net.gridtech.machine.model.entity

import net.gridtech.core.data.INode
import net.gridtech.machine.model.IEntity
import net.gridtech.machine.model.entityClass.TunnelClass


class Tunnel : IEntity<TunnelClass> {
    constructor(node: INode) : super(node)
    constructor(id: String, t: TunnelClass) : super(id, t)

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
                    Tunnel(node)
                else
                    null
    }
}