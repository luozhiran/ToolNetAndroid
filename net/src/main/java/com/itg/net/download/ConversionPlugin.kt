package com.itg.net.download

import android.app.Activity
import android.util.Log
import com.itg.net.DdNet
import com.itg.net.download.interfaces.IProgressCallback
import com.itg.net.download.interfaces.ITask
import com.itg.net.download.operations.PrincipalLife

class ConversionPlugin(val task: BusinessTask) {

    fun setProgressListener(progressBack: IProgressCallback): ConversionPlugin {
        TaskCallbackMgr.instance.setProgressCallback(task, progressBack)
        return this
    }

    fun autoCancel(activity: Activity?): ConversionPlugin {
        PrincipalLife.observeActivityLife(task,activity)
        return this
    }

    fun start(): ITask {
        val taskState = DdNet.instance.download.dispatchTool.getTaskState()
        // 校验任务是否为无效任务
        if (taskState.isInvalidTask(task)) {
            task.progressCallback()?.onFail(ERROR_TAG_7, task)
            PrincipalLife.removeProgressCallback(task)
            return task
        }
        // 校验请求地址是否正在下载
        if (taskState.exitRunningUrl(task.url())) {
            task.progressCallback()?.onFail(ERROR_TAG_8, task)
            PrincipalLife.removeProgressCallback(task)
            return task
        }
        // 校验请求地址是否已经在任务队列
        if (taskState.exitWaitUrl(task.url())) {
            task.progressCallback()?.onFail(ERROR_TAG_10, task)
            PrincipalLife.removeProgressCallback(task)
            return task
        }
        // 下载任务是否启动断点续传
        if (taskState.isBreakpointContinuation(task)) {
            DdNet.instance.download.dispatchTool.appendDownload(task)
            return task
        }
        DdNet.instance.download.dispatchTool.download(task)
        return task

    }


}