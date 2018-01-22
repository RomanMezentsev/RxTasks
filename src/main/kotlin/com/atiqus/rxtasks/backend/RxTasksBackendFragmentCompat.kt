package com.atiqus.rxtasks.backend

import android.support.v4.app.Fragment

class RxTasksBackendFragmentCompat : Fragment(), RxTasksBackend {
	override val tasks = RxTasksStorage()

	init {
		retainInstance = true
	}

	override fun onDestroy() {
		super.onDestroy()

		tasks.unsubscribeAll()
	}
}
