package net.gridtech.machine.model.entityClass

import net.gridtech.core.data.INodeClass
import net.gridtech.machine.model.IEntityClass
import net.gridtech.machine.model.entityField.CabinEmptyField
import net.gridtech.machine.model.entityField.CabinStorageField


class CabinClass(nodeClass: INodeClass) : IEntityClass(nodeClass) {
    companion object {
        val tags = listOf("cabin class")
        fun create(nodeClass: INodeClass): CabinClass? =
                if (nodeClass.tags.containsAll(tags))
                    CabinClass(nodeClass)
                else
                    null

        fun add(name: String, alias: String) =
                add(name, alias, false, tags, null)
                        ?.apply {
                            CabinStorageField.add(this.id)
                            CabinEmptyField.add(this.id)
                        }
    }
}