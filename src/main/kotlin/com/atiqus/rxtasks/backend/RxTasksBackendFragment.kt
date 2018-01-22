package com.atiqus.rxtasks.backend

import android.app.Fragment

class RxTasksBackendFragment : Fragment(), RxTasksBackend {
	override val tasks = RxTasksStorage()

	init {
		retainInstance = true
	}

	override fun onDestroy() {
		super.onDestroy()

		tasks.unsubscribeAll()
	}
}
