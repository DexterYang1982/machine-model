package net.gridtech.machine.model.entityField

import net.gridtech.core.util.compose
import net.gridtech.machine.model.EntityFieldValue
import net.gridtech.machine.model.IEmbeddedEntityField
import net.gridtech.machine.model.IEntityClass

class ModbusSlaveConnectionField(entityClass: IEntityClass) : IEmbeddedEntityField<Boolean>(compose(entityClass.id, RunningStatusField.key)) {
    companion object {
        const val key = "slave-connection"

        fun add(entityClassId: String) = add(
                key = key,
                nodeClassId = entityClassId,
                name = "slave connection",
                alias = "connection",
                tags = emptyList(),
                through = true,
                description = null
        )
    }

    override fun createFieldValue(entityId: String): EntityFieldValue<Boolean> =
            object : EntityFieldValue<Boolean>(entityId, id, { fieldValue ->
                fieldValue.value == "true"
            }) {}
}