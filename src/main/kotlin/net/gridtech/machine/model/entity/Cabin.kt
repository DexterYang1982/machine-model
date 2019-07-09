package net.gridtech.machine.model.entity

import net.gridtech.core.data.INode
import net.gridtech.core.util.generateId
import net.gridtech.machine.model.IBaseProperty
import net.gridtech.machine.model.IEntity
import net.gridtech.machine.model.entityClass.CabinClass
import net.gridtech.machine.model.entityField.CabinEmptyField
import net.gridtech.machine.model.entityField.CabinStorageField
import net.gridtech.machine.model.property.entity.CabinDefinition
import net.gridtech.machine.model.property.entity.CabinDefinitionDescription


class Cabin(node: INode) : IEntity(node) {

    val cabinDefinition = CabinDefinitionDescription(this)
    override fun getDescriptionProperty(): IBaseProperty<*, INode>? = cabinDefinition

    val cabinStorage
        get() = getEntityField<CabinStorageField>(source.nodeClassId, CabinStorageField.key).getFieldValue(source.id)
    val cabinEmpty
        get() = getEntityField<CabinEmptyField>(source.nodeClassId, CabinEmptyField.key).getFieldValue(source.id)

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
                        nodeClassId = cabinClass.source.id,
                        name = name,
                        alias = alias,
                        tags = tags,
                        externalNodeIdScope = emptyList(),
                        externalNodeClassTagScope = emptyList(),
                        description = CabinDefinition.empty()
                )?.apply {
                    getEntityField<CabinStorageField>(cabinClass.source.id, CabinStorageField.key)
                            .createFieldValue(this.id).update(emptyList())
                    getEntityField<CabinEmptyField>(cabinClass.source.id, CabinEmptyField.key)
                            .createFieldValue(this.id).update(false)
                }
    }
}