package com.itg.net.download.operations

import android.app.Activity
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.itg.net.download.DEBUG_TAG
import com.itg.net.download.Task
import com.itg.net.download.TaskCallbackMgr
import com.itg.net.download.data.LockData
import com.itg.net.tools.ThreadTool
import okhttp3.Call
import kotlin.collections.LinkedHashMap

object PrincipalLife {
    private val taskWeakHash by lazy { LinkedHashMap<Activity, MutableList<Task>>() }
    private val callWeakHash by lazy { LinkedHashMap<Activity, MutableList<Call>>() }
    private val lockTask = LockData()
    private val lockCall = LockData()

    fun observeActivityLife(task: Task?, activity: Activity?) {
        if (task == null || activity == null) return
        if (!taskWeakHash.containsKey(activity)) {
            val taskList: MutableList<Task> = mutableListOf()
            taskWeakHash[activity] = taskList
            (activity as? ComponentActivity)?.lifecycle?.addObserver(object :
                LifecycleEventObserver {
                override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                    if (event == Lifecycle.Event.ON_DESTROY) {
                        ThreadTool.runOnExecutor {
                            synchronized(lockTask) {
                                taskWeakHash[activity]?.iterator()?.apply {
                                    while (hasNext()) {
                                        TaskCallbackMgr.instance.removeProgressCallback(next())
                                    }
                                }
                                taskWeakHash.remove(activity)
                            }
                            synchronized(lockCall) {
                                callWeakHash[activity]?.iterator()?.apply {
                                    while (hasNext()) {
                                        next().cancel()
                                    }
                                }
                                callWeakHash.remove(activity)
                            }

                            (activity as? ComponentActivity)?.lifecycle?.removeObserver(this)
                        }
                    }
                }
            })
        }
        val taskList = taskWeakHash[activity]
        taskList?.add(task)
        Log.e("fdsafasdf", "${taskList?.size}")
    }

    fun observeActivityLife(call: Call?, activity: Activity?) {
        if (call == null || activity == null) return
        if (!callWeakHash.containsKey(activity)) {
            val taskList: MutableList<Call> = mutableListOf()
            callWeakHash[activity] = taskList
            (activity as? ComponentActivity)?.lifecycle?.addObserver(object :
                LifecycleEventObserver {
                override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                    if (event == Lifecycle.Event.ON_DESTROY) {
                        ThreadTool.runOnExecutor {
                            synchronized(lockCall) {
                                callWeakHash[activity]?.iterator()?.apply {
                                    while (hasNext()) {
                                        next().cancel()
                                    }
                                }
                                callWeakHash.remove(activity)
                                (activity as? ComponentActivity)?.lifecycle?.removeObserver(this)
                            }
                        }
                    }
                }
            })
        }
        val taskList = callWeakHash[activity]
        taskList?.add(call)
    }

    fun removeProgressCallback(task: Task?) {
        if (task == null) return
        TaskCallbackMgr.instance.removeProgressCallback(task)
        val iterator = taskWeakHash.iterator()
        var entryValue:MutableList<Task>?=null
        while (iterator.hasNext()) {
            entryValue = iterator.next().value
            if (entryValue.remove(task)) {
                if (entryValue.size == 0) {
                    iterator.remove()
                }
                break
            }
        }
    }

    fun removeCall(call: Call?) {
        if (call == null) return
        val iterator = callWeakHash.iterator()
        var entryValue:MutableList<Call>?=null
        while (iterator.hasNext()) {
            entryValue = iterator.next().value
            entryValue.remove(call)
            if (entryValue.size == 0) {
                iterator.remove()
            }
        }
    }

    fun debugPrint(){
        Log.i(DEBUG_TAG,"生命周期：下载任务数${taskWeakHash.size}， 请求接口数：${callWeakHash.size}")
    }


}