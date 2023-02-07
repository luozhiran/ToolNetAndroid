package com.itg.net

import com.itg.net.download.BusinessTask
import com.itg.net.download.DTask
import com.itg.net.download.DispatchTool
import com.itg.net.download.interfaces.IProgressCallback
import com.itg.net.download.interfaces.Task

class Download {

    val dispatchTool: DispatchTool by lazy { DispatchTool() }

    fun downloadTask(): DTask {
        return BusinessTask()
    }

    fun setProgressListener(url: String, progressBack: IProgressCallback) {
        DdNet.instance.callbackMgr.addProgressCallbackToMap(url, progressBack)
    }

    fun remoteProgressListener(url: String) {
        DdNet.instance.callbackMgr.removeProgressBackForMap(url)
    }

    fun isQueue(url: String): Boolean {
        return dispatchTool.isQueue(url)
    }

    fun cancel(url: String) {
        dispatchTool.cancelTask(url)
    }

    fun getTask(url: String): Task? {
        return dispatchTool.getTask(url)
    }

}