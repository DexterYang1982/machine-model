package net.gridtech.machine.model.entityClass

import net.gridtech.core.data.INodeClass
import net.gridtech.machine.model.IEntityClass
import net.gridtech.machine.model.entityField.CabinEmptyField
import net.gridtech.machine.model.entityField.CabinStorageField


class CabinClass(id: String) : IEntityClass(id) {
    val empty = CabinEmptyField(this)
    val storage = CabinStorageField(this)

    fun addNew(name: String, alias: String) =
        addNew(name, alias, tags, false)

    companion object {
        val tags = listOf("cabin class")
        fun create(nodeClass: INodeClass): CabinClass? =
                if (nodeClass.tags.containsAll(tags))
                    CabinClass(nodeClass.id).apply { initialize(nodeClass) }
                else
                    null
    }
}