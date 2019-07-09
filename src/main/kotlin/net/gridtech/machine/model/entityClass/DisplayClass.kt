package net.gridtech.machine.model.entityClass

import net.gridtech.core.data.INodeClass
import net.gridtech.machine.model.IBaseProperty
import net.gridtech.machine.model.IEntityClass
import net.gridtech.machine.model.property.entityClass.DisplayClientVersionDescription


class DisplayClass(nodeClass: INodeClass) : IEntityClass(nodeClass) {

    val displayClientVersionDescription = DisplayClientVersionDescription(this)
    override fun getDescriptionProperty(): IBaseProperty<*, INodeClass>? = displayClientVersionDescription

    companion object {
        val tags = listOf("display class")
        fun create(nodeClass: INodeClass): DisplayClass? =
                if (nodeClass.tags.containsAll(tags))
                    DisplayClass(nodeClass)
                else
                    null

        fun add(name: String, alias: String) =
                add(name, alias, true, tags, null)
    }
}