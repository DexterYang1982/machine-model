package net.gridtech.machine.model.entityClass

import net.gridtech.core.data.INodeClass
import net.gridtech.machine.model.IBaseProperty
import net.gridtech.machine.model.IEntityClass
import net.gridtech.machine.model.entityField.RunningStatusField
import net.gridtech.machine.model.entityField.SecretField
import net.gridtech.machine.model.property.entityClass.DisplayClientVersionDescription


class DisplayClass(id: String) : IEntityClass(id) {

    val runningStatus = RunningStatusField(this)
    val secret = SecretField(this)

    val description = DisplayClientVersionDescription(this)
    override fun getDescriptionProperty(): IBaseProperty<*, INodeClass>? = description


    fun addNew(name: String, alias: String) =
            addNew(name, alias, tags, true)

    companion object {
        val tags = listOf("display class")
        fun create(nodeClass: INodeClass): DisplayClass? =
                if (nodeClass.tags.containsAll(tags))
                    DisplayClass(nodeClass.id).apply { initialize(nodeClass) }
                else
                    null
    }
}