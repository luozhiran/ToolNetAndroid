package com.itg.net.download.operations

import android.app.Activity
import androidx.activity.ComponentActivity
import com.itg.net.download.Task
import com.itg.net.download.TaskCallbackMgr
import com.itg.net.download.interfaces.ITask
import com.itg.net.reqeust.MyLifecycleEventObserver
import okhttp3.Call

class PrincipalLife {
    private var activity: Activity? = null

    private val lifeObservable by lazy { MyLifecycleEventObserver() }

    fun addActivity(activity: Activity?): PrincipalLife {
        this.activity = activity
        return this
    }

    fun registerEvent(call: Call?, task: ITask) {
        if (call == null) return
        if (activity as? ComponentActivity? == null) return
        val callback = {
            call.cancel()
            unregisterEvent(task)
            TaskCallbackMgr.instance.removeProgressCallback(task as Task)
        }
        activity?.runOnUiThread {
            lifeObservable.setCallback(callback)
            (activity as? ComponentActivity)?.lifecycle?.addObserver(lifeObservable)
        }
    }

    fun unregisterEvent(task: ITask?) {
        if (task is Task) {
            TaskCallbackMgr.instance.removeProgressCallback(task)
        }
        val tempActivity = (activity as? ComponentActivity?)
        tempActivity?.runOnUiThread {
            tempActivity.lifecycle.removeObserver(lifeObservable)
            activity = null
        }
    }


    fun registerEvent(call: Call?) {
        if (call == null) return
        val tempActivity = (activity as? ComponentActivity)
        lifeObservable.setCallback {
            call.cancel()
            unregisterEvent()
        }
        tempActivity?.lifecycle?.addObserver(lifeObservable)
    }


    fun unregisterEvent() {
        val tempActivity = (activity as? ComponentActivity)
        tempActivity?.runOnUiThread {
            tempActivity.lifecycle.removeObserver(lifeObservable)
            activity = null
        }
    }

}