package net.gridtech.machine.model

import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import net.gridtech.core.data.ChangedType
import net.gridtech.core.data.INodeClass

abstract class EntityClass(initNodeClass: INodeClass, observable: Observable<Triple<ChangedType, String, INodeClass?>>) {
    val id = initNodeClass.id
    private val updatePublisher = Observable.concat(
            Observable.just(initNodeClass),
            observable.filter { it.first == ChangedType.UPDATE && it.second == id }.map { it.third!! }
    ).publish()


    private val deleteObservable = observable.filter { it.first == ChangedType.DELETE && it.second == id }.firstOrError()
    private lateinit var updateDisposable: Disposable

    init {
        observable.doOnEach { }
    }

    fun start() {
        updateDisposable = updatePublisher.connect()
        deleteObservable.subscribe { _, _ ->
            updateDisposable.dispose()
        }
    }

    fun onDelete(cleanUpFunction: (id: String) -> Unit) {
        deleteObservable.subscribe { _, _ -> cleanUpFunction(id) }
    }
}
