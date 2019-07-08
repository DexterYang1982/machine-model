package net.gridtech.machine.model.entityField

import net.gridtech.core.data.IField
import net.gridtech.core.util.KEY_FIELD_RUNNING_STATUS
import net.gridtech.machine.model.IEntityField

class RunningStatusField(field: IField) : IEntityField(field) {
    companion object {
        val key = KEY_FIELD_RUNNING_STATUS
        fun create(field: IField): RunningStatusField? =
                if (field.match(key))
                    RunningStatusField(field)
                else
                    null
    }
}