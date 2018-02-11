package com.atiqus.rxtasks

class RxTaskValue<out T> private constructor (private val type: Type, private val value: Any?) {
	companion object {
		fun <T> progress(current: Int, total: Int) = RxTaskValue<T>(Type.PROGRESS, Pair(current, total))

		fun <T> value(value: T) = RxTaskValue<T>(Type.VALUE, value)
	}

	fun isProgress() = type == Type.PROGRESS

	@Suppress("UNCHECKED_CAST")
	fun getProgress() = value as Pair<Int, Int>

	@Suppress("UNCHECKED_CAST")
	fun getValue() = value as T

	private enum class Type {
		PROGRESS,
		VALUE
	}
}
