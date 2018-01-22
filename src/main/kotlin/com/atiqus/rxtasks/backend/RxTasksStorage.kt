package com.atiqus.rxtasks.backend

import com.atiqus.rxtasks.RxTask

class RxTasksStorage {
	private val tasks = mutableListOf<RxTask<*>>()

	fun unsubscribeAll() {
		tasks.forEach(RxTask<*>::unsubscribe)
	}

	@Suppress("UNCHECKED_CAST")
	fun <T> findTask(tag: String) = findTaskByTag(tag) as? RxTask<T>

	fun addTask(task: RxTask<*>) {
		findTaskByTag(task.tag)?.let { throw IllegalStateException("Task with tag '${task.tag}' already registered") }
		tasks += task
	}

	private fun findTaskByTag(tag: String) = tasks.firstOrNull { it.tag == tag }
}
