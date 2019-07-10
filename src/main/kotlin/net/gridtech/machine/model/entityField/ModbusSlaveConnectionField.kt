package net.gridtech.machine.model.entityField

import net.gridtech.machine.model.EntityFieldValue
import net.gridtech.machine.model.IEmbeddedEntityField
import net.gridtech.machine.model.IEntityClass

class ModbusSlaveConnectionField(entityClass: IEntityClass) : IEmbeddedEntityField<Boolean>(entityClass.id, key) {
    override fun defaultValue(): Boolean = false
    override fun autoAddNew(): Boolean = true
    override fun autoInitValue(): Boolean = true
    override fun addNew() {
        addNew(
                "slave connection",
                "connection",
                emptyList(),
                true,
                null)
    }

    companion object {
        const val key = "slave-connection"
    }

    override fun createFieldValue(entityId: String): EntityFieldValue<Boolean> =
            object : EntityFieldValue<Boolean>(entityId, id, { fieldValue ->
                fieldValue.value == "true"
            }) {}
}