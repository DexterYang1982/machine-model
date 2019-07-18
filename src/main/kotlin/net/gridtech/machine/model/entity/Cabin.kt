package net.gridtech.machine.model.entity

import net.gridtech.core.data.INode
import net.gridtech.core.util.cast
import net.gridtech.machine.model.DataHolder
import net.gridtech.machine.model.IEntity
import net.gridtech.machine.model.entityClass.CabinClass
import net.gridtech.machine.model.property.entity.CabinDefinitionDescription


class Cabin(id: String, entityClass: CabinClass) : IEntity<CabinClass>(id, entityClass) {
    override val description = CabinDefinitionDescription(this)
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
        val tags = listOf("cabin")
        fun create(node: INode): Cabin? =
                if (node.tags.containsAll(tags))
                    Cabin(node.id, cast(DataHolder.instance.entityClassHolder[node.nodeClassId])!!).apply { initialize(node) }
                else
                    null
    }
}