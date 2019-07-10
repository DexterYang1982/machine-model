package net.gridtech.machine.model.entity

import net.gridtech.core.data.INode
import net.gridtech.core.util.generateId
import net.gridtech.machine.model.IBaseProperty
import net.gridtech.machine.model.IEntity
import net.gridtech.machine.model.entityClass.CabinClass
import net.gridtech.machine.model.property.entity.CabinDefinition
import net.gridtech.machine.model.property.entity.CabinDefinitionDescription


class Cabin(node: INode) : IEntity<CabinClass>(node) {

    val description = CabinDefinitionDescription(this)
    override fun getDescriptionProperty(): IBaseProperty<*, INode>? = description

    companion object {
        val tags = listOf("cabin")
        fun create(node: INode): Cabin? =
                if (node.tags.containsAll(tags))
                    Cabin(node)
                else
                    null

        fun add(cabinClass: CabinClass, parentId: String, name: String, alias: String) =
                add(
                        id = generateId(),
                        parentId = parentId,
                        nodeClassId = cabinClass.id,
                        name = name,
                        alias = alias,
                        tags = tags,
                        externalNodeIdScope = emptyList(),
                        externalNodeClassTagScope = emptyList(),
                        description = CabinDefinition.empty()
                )?.apply {
                }
    }
}