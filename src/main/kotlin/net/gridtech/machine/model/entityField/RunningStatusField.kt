package net.gridtech.machine.model.entityField

import io.reactivex.subjects.PublishSubject
import net.gridtech.core.data.IField
import net.gridtech.core.data.IFieldValue
import net.gridtech.core.util.INTERVAL_RUNNING_STATUS_REPORT
import net.gridtech.core.util.KEY_FIELD_RUNNING_STATUS
import net.gridtech.core.util.currentTime
import net.gridtech.machine.model.EntityFieldValue
import net.gridtech.machine.model.IEntityField
import java.util.concurrent.TimeUnit

class RunningStatusField(field: IField) : IEntityField<Boolean>(field) {
    companion object {
        val key = KEY_FIELD_RUNNING_STATUS
        fun create(field: IField): RunningStatusField? =
                if (field.match(key))
                    RunningStatusField(field)
                else
                    null
    }

    override fun createFieldValue(entityId: String): EntityFieldValue<Boolean> =
            object : EntityFieldValue<Boolean>(entityId, source.id, {
                (currentTime() - it.updateTime) < INTERVAL_RUNNING_STATUS_REPORT * 2
            }) {
                private val counter = PublishSubject.create<IFieldValue>()

                init {
                    counter.debounce(INTERVAL_RUNNING_STATUS_REPORT * 2, TimeUnit.MILLISECONDS)
                            .subscribe {
                                if (value == true) {
                                    _value = false
                                    publish(false)
                                }
                            }
                }

                override fun sourceUpdated(s: IFieldValue) {
                    super.sourceUpdated(s)
                    counter.onNext(s)
                }

                override fun onDelete() {
                    super.onDelete()
                    counter.onComplete()
                }
            }
}