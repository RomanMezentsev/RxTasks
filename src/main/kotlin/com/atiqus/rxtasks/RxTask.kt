package com.atiqus.rxtasks

import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class RxTask<T>(val tag: String) {
	private var started = false
	private var cacheable = false
	private var completed = false
	private var result: T? = null
	private var hasResult = false
	private var error: Throwable? = null
	private var resultListener: RxTaskResult<T>? = null
	private var disposable: Disposable? = null

	fun subscribe(resultListener: RxTaskResult<T>) {
		this.resultListener = resultListener
		if (completed) {
			deliverResult()
		}
	}

	fun subscribe(onResultAction: (RxTask<T>, T) -> Unit, onErrorAction: ((RxTask<T>, Throwable) -> Unit)?) {
		subscribe(object : RxTaskResult<T> {
			override fun onResult(task: RxTask<T>, result: T) {
				onResultAction(task, result)
			}

			override fun onError(task: RxTask<T>, error: Throwable) {
				onErrorAction?.let {
					it(task, error)
					return
				}
				super.onError(task, error)
			}
		})
	}

	fun subscribe(onResultAction: (RxTask<T>, T) -> Unit, onErrorAction: (Throwable) -> Unit) {
		subscribe(onResultAction, { _, error -> onErrorAction(error) })
	}

	fun subscribe(onResultAction: (T) -> Unit, onErrorAction: (RxTask<T>, Throwable) -> Unit) {
		subscribe({ _, result -> onResultAction(result) }, onErrorAction)
	}

	fun subscribe(onResultAction: (T) -> Unit, onErrorAction: (Throwable) -> Unit) {
		subscribe({ _, result -> onResultAction(result) }, { _, error -> onErrorAction(error) } )
	}

	fun subscribe(onResultAction: (RxTask<T>, T) -> Unit) {
		subscribe(onResultAction, null)
	}

	fun subscribe(onResultAction: (T) -> Unit) {
		subscribe { _, result -> onResultAction(result) }
	}

	fun unsubscribe() {
		unsubscribeInternal()
		resultListener = null
	}

	fun reset() {
		started = false
		completed = false
		result = null
		hasResult = false
		error = null
	}

	fun start(observable: Observable<T>, cacheable: Boolean = true) {
		if (started) {
			return
		}

		reset()
		started = true
		this.cacheable = cacheable

		observable
				.subscribeOn(Schedulers.computation())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(TaskObserver())
	}

	fun restart(observable: Observable<T>, cacheable: Boolean = true) {
		unsubscribeInternal()
		reset()
		start(observable, cacheable)
	}

	fun isStarted() = started

	fun isCompleted() = completed

	fun isRunning() = isStarted() && !isCompleted()

	private fun unsubscribeInternal() {
		disposable?.let {
			if (!it.isDisposed) {
				it.dispose()
			}
			disposable = null
		}
	}

	private fun deliverResult() {
		if (!completed) {
			return
		}
		resultListener?.let { listener ->
			error?.let {
				listener.onError(this, it)
				if (!cacheable) {
					reset()
				}
				return
			}
			if (hasResult) {
				listener.onResult(this, result!!)
				if (!cacheable) {
					reset()
				}
			}
		}
	}

	private inner class TaskObserver : Observer<T> {
		override fun onSubscribe(disposable: Disposable) {
			this@RxTask.disposable = disposable
		}

		override fun onNext(item: T) {
			result = item
			hasResult = true
		}

		override fun onComplete() {
			completed = true
			deliverResult()
		}

		override fun onError(error: Throwable) {
			this@RxTask.error = error
			completed = true
			deliverResult()
		}
	}
}
