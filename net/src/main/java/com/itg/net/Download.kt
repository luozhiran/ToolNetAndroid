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

    /**
     * 监听所有下载任务的回调
     * @param progressBack IProgressCallback
     */
    fun setGlobalProgressListener(progressBack: IProgressCallback) {
        DdNet.instance.callbackMgr.addProgressCallback(progressBack)
    }

    /**
     * 和setGlobalProgressListener配套使用
     * @param progressBack IProgressCallback
     */
    fun remoteGlobalProgressListener(progressBack: IProgressCallback) {
        DdNet.instance.callbackMgr.removeProgressBack(progressBack)
    }

    fun isQueue(url: String): Boolean {
        return dispatchTool.isQueue(url)
    }

    fun cancel(url: String?) {
        dispatchTool.cancelTask(url)
    }

    fun cancel(task: Task?) {
        dispatchTool.cancelTask(task)
    }

    fun getTask(url: String): Task? {
        return dispatchTool.getTask(url)
    }

}