package net.gridtech.machine.model.entity

import net.gridtech.core.data.INode
import net.gridtech.machine.model.IBaseProperty
import net.gridtech.machine.model.IEntity
import net.gridtech.machine.model.entityClass.CabinClass
import net.gridtech.machine.model.property.entity.CabinDefinitionDescription


class Cabin : IEntity<CabinClass> {
    constructor(node: INode) : super(node)
    constructor(id: String, t: CabinClass) : super(id, t)
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
                    Cabin(node)
                else
                    null
    }
}