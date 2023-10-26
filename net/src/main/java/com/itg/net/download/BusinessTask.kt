package com.itg.net.download

import android.app.Activity
import com.itg.net.DdNet
import com.itg.net.download.interfaces.IProgressCallback
import com.itg.net.download.interfaces.ITask
import com.itg.net.download.operations.DownloadEndNotify
import com.itg.net.download.operations.PrincipalLife
import okhttp3.Call
import java.lang.ref.WeakReference

class BusinessTask : Task() {
    private var activity: WeakReference<Activity>? = null

    init {
        registerProgressListener()
    }

    private fun registerProgressListener(): BusinessTask {
        progressBack(object : IProgressCallback {
            override fun onConnecting(task: ITask?) {
                DownloadEndNotify.connectNotify(task)
            }

            override fun onProgress(task: ITask?) {
                if (!DdNet.instance.download.dispatchTool.getTaskState().downloadComplete(task!!)) {
                    DownloadEndNotify.progressNotify(task)
                } else {
                    DownloadEndNotify.completeNotify(task)
                }
            }

            override fun onFail(error: String?, task: ITask?) {
                DownloadEndNotify.failNotify(task, error)
            }
        })
        return this
    }

    fun autoCancel(activity: Activity?): BusinessTask {
        activity?.let {
            this.activity = WeakReference<Activity>(it)
        }
        return this
    }

    fun getActivity(): Activity? {
        return this.activity?.get()
    }

    fun prepareEnd(): ConversionPlugin {
        return ConversionPlugin(this)
    }

}