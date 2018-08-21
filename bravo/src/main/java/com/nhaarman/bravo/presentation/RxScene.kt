package com.nhaarman.bravo.presentation

import android.support.annotation.CallSuper
import arrow.core.Option
import arrow.core.toOption
import com.nhaarman.bravo.BravoBundle
import com.nhaarman.bravo.StateRestorable
import com.nhaarman.bravo.StateSaveable
import com.nhaarman.bravo.presentation.RxScene.Event.Attached
import com.nhaarman.bravo.presentation.RxScene.Event.Detached
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observables.ConnectableObservable
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.subjects.BehaviorSubject

/**
 * An abstract [Scene] implementation which provides a basic Rx implementation.
 *
 * Implementers of this class gain access to the [view] property to get notified
 * of view changes, and a [disposables] property is provided for easy clearing
 * of [io.reactivex.disposables.Disposable]s.
 *
 * @see SaveableScene
 */
abstract class RxScene<V>(
    savedState: BravoBundle?
) : SaveableScene<V>(savedState), StateSaveable
    where V : Container, V : StateRestorable {

    /**
     * A disposable container which will be cleared when this Scene receives a
     * call to [onStop].
     * Note that the instance will be _cleared_ not disposed, so this instance
     * can be reused multiple times.
     *
     * @see [CompositeDisposable.clear]
     */
    protected val disposables = CompositeDisposable()

    /**
     * A disposable container which will be disposed when this Scene receives a
     * call to [onDestroy].
     * Note that the instance will be _disposed_: Disposables added after
     * [onDestroy] is called will immediately be disposed.
     *
     * @see [CompositeDisposable.dispose]
     */
    private val sceneDisposables = CompositeDisposable()

    private val eventsSubject = BehaviorSubject.createDefault<Event<V>>(Detached)

    /**
     * Publishes a stream of optional [V] instances that are attached to this
     * Scene.
     */
    protected val view: Observable<Option<V>> = eventsSubject
        .map { event ->
            when (event) {
                is Attached<V> -> event.v.toOption()
                is Detached -> Option.empty()
            }
        }
        .replay(1).autoConnect()

    @CallSuper
    override fun attach(v: V) {
        super.attach(v)
        eventsSubject.onNext(Attached(v))
    }

    @CallSuper
    override fun detach(v: V) {
        eventsSubject.onNext(Detached)
        super.detach(v)
    }

    @CallSuper
    override fun onStop() {
        disposables.clear()
    }

    @CallSuper
    override fun onDestroy() {
        sceneDisposables.dispose()
    }

    @Suppress("unused")
    private sealed class Event<out V> {
        class Attached<V>(val v: V) : Event<V>()
        object Detached : Event<Nothing>()
    }

    /**
     * A utility function to combine an arbitrary stream of [T]'s with the
     * currently attached [V] instance.
     */
    protected fun <T> Observable<T>.combineWithLatestView(): Observable<Pair<T, V?>> {
        return Observables.combineLatest(this, view) { t, v -> t to v.orNull() }
    }

    /**
     * A utility function to switchMap the stream of attached [V] instances to
     * streams that [V] provides.
     */
    protected fun <R> Observable<Option<V>>.whenAvailable(f: (V) -> Observable<R>): Observable<R> {
        return switchMap { v -> v.orNull()?.let(f) ?: Observable.empty() }
    }

    /**
     * Returns an Observable that automatically connects at most once to the
     * receiving [ConnectableObservable] when the first Observer subscribes to it.
     * The connection will automatically be disposed of when [scene] is destroyed.
     *
     * @see [ConnectableObservable.autoConnect].
     */
    protected fun <T> ConnectableObservable<T>.autoConnect(scene: RxScene<V>): Observable<T> {
        return autoConnect(1) { scene.sceneDisposables += it }
    }
}
