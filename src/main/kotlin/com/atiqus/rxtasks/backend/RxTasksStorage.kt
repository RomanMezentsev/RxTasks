package com.atiqus.rxtasks.backend

import com.atiqus.rxtasks.RxTask

class RxTasksStorage {
	private val tasks = mutableMapOf<String, RxTask<*>>()

	fun unsubscribeAll() {
		tasks.values.forEach(RxTask<*>::unsubscribe)
	}

	@Suppress("UNCHECKED_CAST")
	fun <T> findTask(tag: String) = tasks[tag] as? RxTask<T>

	fun addTask(tag: String, task: RxTask<*>) {
		if (tag in tasks) {
			throw IllegalStateException("Task with tag '$tag' already registered")
		}
		tasks += tag to task
	}

	fun removeTask(tag: String) {
		tasks.remove(tag)?.let(RxTask<*>::unsubscribe)
	}
}
