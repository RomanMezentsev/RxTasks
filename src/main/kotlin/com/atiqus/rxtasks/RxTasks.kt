package com.atiqus.rxtasks

import android.app.Activity
import android.app.Fragment
import android.app.FragmentManager
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.FragmentActivity
import com.atiqus.rxtasks.backend.RxTasksBackend
import com.atiqus.rxtasks.backend.RxTasksBackendFragment
import com.atiqus.rxtasks.backend.RxTasksBackendFragmentCompat

class RxTasks private constructor(private val backend: RxTasksBackend) {
	companion object {
		private val FRAGMENT_TAG = RxTasks::class.java.name + ".TASKS_BACKEND_FRAGMENT"
		private const val TASK_DEFAULT_TAG = "DEFAULT_TASK"

		fun get(activity: Activity) = get(activity.fragmentManager)

		@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
		fun get(fragment: Fragment) = get(fragment.childFragmentManager)

		fun get(activity: FragmentActivity) = get(activity.supportFragmentManager)

		fun get(fragment: android.support.v4.app.Fragment) = get(fragment.childFragmentManager)

		private fun get(fragmentManager: FragmentManager) = get(FragmentManagerWrapperImpl(fragmentManager))

		private fun get(fragmentManager: android.support.v4.app.FragmentManager) =
				get(FragmentManagerWrapperImplCompat(fragmentManager))

		private fun <T : Any> get(wrapper: FragmentManagerWrapper<T>): RxTasks {
			var backendFragment = wrapper.findByTag(FRAGMENT_TAG)
			if (backendFragment == null) {
				backendFragment = wrapper.create()
				wrapper.add(backendFragment, FRAGMENT_TAG)
			}
			if (backendFragment !is RxTasksBackend) {
				throw IllegalStateException("Backend fragment tagged '${FRAGMENT_TAG}' has wrong type: " +
						backendFragment.javaClass.name)
			}
			return RxTasks(backendFragment)
		}
	}

	fun <T> task(taskTag: String = TASK_DEFAULT_TAG): RxTask<T> {
		var task = backend.tasks.findTask<T>(taskTag)
		if (task == null) {
			task = RxTask(taskTag)
			backend.tasks.addTask(task)
		}
		return task
	}

	private interface FragmentManagerWrapper<T> {
		fun findByTag(tag: String): T?
		fun create(): T
		fun add(fragment: T, tag: String)
	}

	private class FragmentManagerWrapperImpl(private val fm: FragmentManager) :
			FragmentManagerWrapper<Fragment> {
		override fun findByTag(tag: String): Fragment? = fm.findFragmentByTag(tag)

		override fun create() = RxTasksBackendFragment()

		override fun add(fragment: Fragment, tag: String) {
			fm.beginTransaction().add(fragment, tag).commit()
		}
	}

	private class FragmentManagerWrapperImplCompat(private val fm: android.support.v4.app.FragmentManager) :
			FragmentManagerWrapper<android.support.v4.app.Fragment> {
		override fun findByTag(tag: String): android.support.v4.app.Fragment? = fm.findFragmentByTag(tag)

		override fun create(): android.support.v4.app.Fragment = RxTasksBackendFragmentCompat()

		override fun add(fragment: android.support.v4.app.Fragment, tag: String) {
			fm.beginTransaction().add(fragment, tag).commit()
		}
	}
}
