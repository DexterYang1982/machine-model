package net.gridtech.machine.model.entityField

import io.reactivex.subjects.PublishSubject
import net.gridtech.core.data.IFieldValue
import net.gridtech.core.util.INTERVAL_RUNNING_STATUS_REPORT
import net.gridtech.core.util.KEY_FIELD_RUNNING_STATUS
import net.gridtech.core.util.currentTime
import net.gridtech.machine.model.EntityFieldValue
import net.gridtech.machine.model.IEmbeddedEntityField
import net.gridtech.machine.model.IEntityClass
import java.util.concurrent.TimeUnit

class RunningStatusField(entityClass: IEntityClass) : IEmbeddedEntityField<Boolean>(entityClass.id, key) {
    override fun defaultValue(): Boolean = false

    companion object {
        const val key = KEY_FIELD_RUNNING_STATUS
    }

    override fun createFieldValue(entityId: String): EntityFieldValue<Boolean> =
            object : EntityFieldValue<Boolean>(entityId, id, {
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

                override fun delete() {
                    super.delete()
                    counter.onComplete()
                }
            }
}