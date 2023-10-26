package com.itg.net.download

import android.app.Activity
import com.itg.net.DdNet
import com.itg.net.download.interfaces.IProgressCallback
import com.itg.net.download.interfaces.ITask
import com.itg.net.download.operations.DownloadEndNotify
import com.itg.net.download.operations.PrincipalLife
import okhttp3.Call

class BusinessTask : Task() {
    private val principalLife by lazy { PrincipalLife() }
    val download = DdNet.instance.download
    init {
        registerProgressListener()
    }

    private fun registerProgressListener(): BusinessTask {
        progressBack(object : IProgressCallback {
            override fun onConnecting(task: ITask?) {
                DownloadEndNotify.connectNotify(task)
            }

            override fun onProgress(task: ITask?) {
                if (!download.dispatchTool.getTaskState().downloadComplete(task!!)) {
                    DownloadEndNotify.progressNotify(task)
                } else {
                    DownloadEndNotify.completeNotify(task)
                    unregisterEvent(task)
                }
            }

            override fun onFail(error: String?, task:ITask?) {
                DownloadEndNotify.failNotify(task,error)
                unregisterEvent(task)

            }
        })
        return this
    }

    fun autoCancel(activity: Activity?): BusinessTask {
        principalLife.addActivity(activity)
        return this
    }
    fun prepareEnd(): ConversionPlugin {
        return ConversionPlugin(this)
    }

    fun registerEvent(call: Call?,task: ITask) {
        principalLife.registerEvent(call,task)
    }

    private fun unregisterEvent(task: ITask?) {
        principalLife.unregisterEvent(task)
    }


}