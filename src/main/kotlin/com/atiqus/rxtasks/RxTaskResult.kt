package com.atiqus.rxtasks

interface RxTaskResult<T> {
	fun onResult(task: RxTask<T>, result: T)

	fun onError(task: RxTask<T>, error: Throwable) {
		throw RuntimeException(error)
	}
}
