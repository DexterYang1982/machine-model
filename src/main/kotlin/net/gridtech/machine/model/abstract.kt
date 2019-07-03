package net.gridtech.machine.model

import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import net.gridtech.core.data.ChangedType
import net.gridtech.core.data.IStructureData

abstract class IBaseStructure<T : IStructureData>(initData: T, observable: Observable<Triple<ChangedType, String, T?>>) {

    val id = initData.id

    protected val updatePublisher = Observable.concat(
            Observable.just(initData),
            observable.filter { it.first == ChangedType.UPDATE && it.second == id }.map { it.third!! }
    ).publish()

    private val deletePublisher = observable.filter { it.first == ChangedType.DELETE && it.second == id }.publish()
    private var updateDisposable: Disposable? = null
    private var deleteDisposable: Disposable? = null

    fun start() {
        deletePublisher.subscribe {
            updateDisposable?.dispose()
            deleteDisposable?.dispose()
        }
        updateDisposable = updatePublisher.connect()
        deleteDisposable = deletePublisher.connect()
    }

    fun onDelete(cleanUpFunction: (id: String) -> Unit) {
        deletePublisher.doOnNext { cleanUpFunction(id) }
    }
}
