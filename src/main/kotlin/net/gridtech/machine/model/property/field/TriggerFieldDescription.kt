package net.gridtech.machine.model.property.field

import net.gridtech.core.data.IField
import net.gridtech.core.util.parse
import net.gridtech.machine.model.EntityWrite
import net.gridtech.machine.model.IBaseProperty
import net.gridtech.machine.model.ReadCondition
import net.gridtech.machine.model.entityField.TriggerField


class TriggerFieldDescription(private val triggerField: TriggerField)
    : IBaseProperty<Trigger, IField>({ parse(it.description) }, Trigger.empty()) {
}

data class Trigger(
        var delay: Long,
        var condition: ReadCondition,
        var execute: EntityWrite
) {
    companion object {
        fun empty() = Trigger(
                -1, ReadCondition.empty(), EntityWrite.empty()
        )
    }
}