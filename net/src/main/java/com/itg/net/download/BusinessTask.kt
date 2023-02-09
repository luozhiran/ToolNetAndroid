package com.itg.net.download

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.os.Build
import androidx.activity.ComponentActivity
import com.itg.net.DdNet
import com.itg.net.Download
import com.itg.net.base.Builder
import com.itg.net.download.interfaces.IProgressCallback
import com.itg.net.download.interfaces.Task
import com.itg.net.reqeust.MyLifecycleEventObserver
import okhttp3.Call

class BusinessTask : DTask() {
    var activity:Activity?=null
    private val lifeObservable by lazy { MyLifecycleEventObserver() }

    init {
        registerProgressListener()
    }

    private fun registerProgressListener(): BusinessTask {
        progressBack(object : IProgressCallback {
            override fun onConnecting(task: Task?) {
                if (task == null) return
                DdNet.instance.callbackMgr.loopConnecting(task as DTask)
            }

            override fun onProgress(task: Task?) {
                if (task == null) return
                DdNet.instance.callbackMgr.loop(task as DTask)
                if (task.getProgress() !=100)return
                unregisterEvent()
                var intent:Intent?=null
                if (task.customBroadcast().orEmpty().isNotBlank()) {
                    intent = Intent(task.customBroadcast())
                } else if (task.broadcast()) {
                    intent = Intent(DdNet.BROAD_ACTION)
                }
                intent?.let {it->
                    if (Build.VERSION.SDK_INT >= 26 && task.broadcastComponentName().orEmpty().isNotBlank()) {
                        it.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP)//加上这句话，可以解决在android8.0系统以上2个module之间发送广播接收不到的问题
                        it.component = DdNet.instance.ddNetConfig.pkgName?.let { task.broadcastComponentName()?.let { it1 -> ComponentName(it, it1) } }
                    }
                    it.putExtra("url", task.url())
                    it.putExtra("file", task.path())
                    it.putExtra("extra", task.extra())
                    DdNet.instance.ddNetConfig.application?.sendBroadcast(it)
                }

            }

            override fun onFail(error: String?, url: String?) {
                DdNet.instance.callbackMgr.loopFail(error ?: "", url ?: "")
                unregisterEvent()

            }
        })
        return this
    }

    fun autoCancel(activity: Activity?): BusinessTask {
        this.activity = activity
        return this
    }
    fun prepareEnd(): ConversionPlugin {
        return ConversionPlugin(this)
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

    private fun unregisterEvent() {
        val tempActivity = (activity as? ComponentActivity)
        tempActivity?.runOnUiThread {
            tempActivity.lifecycle.removeObserver(lifeObservable)
            activity = null
        }
    }


}