package net.gridtech.machine.model.entity

import net.gridtech.core.data.INode
import net.gridtech.machine.model.IBaseProperty
import net.gridtech.machine.model.IEntity
import net.gridtech.machine.model.entityClass.GroupClass
import net.gridtech.machine.model.property.entity.GroupDefinitionDescription


class Group : IEntity<GroupClass> {
    constructor(node: INode) : super(node)
    constructor(id: String, t: GroupClass) : super(id, t)
    override val description = GroupDefinitionDescription(this)

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
        val tags = listOf("group")
        fun create(node: INode): Group? =
                if (node.tags.containsAll(tags))
                    Group(node)
                else
                    null
    }
}