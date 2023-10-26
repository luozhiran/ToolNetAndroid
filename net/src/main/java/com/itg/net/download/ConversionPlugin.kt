package com.itg.net.download

import com.itg.net.DdNet
import com.itg.net.download.interfaces.IProgressCallback
import com.itg.net.download.interfaces.ITask

class ConversionPlugin(val task: BusinessTask) {
    fun setProgressListener(progressBack: IProgressCallback): ConversionPlugin {
        TaskCallbackMgr.instance.setProgressCallback(task, progressBack)
        return this
    }

    fun start(): ITask {
        val taskState = DdNet.instance.download.dispatchTool.getTaskState()
        // 校验任务是否为无效任务
        if (taskState.isInvalidTask(task)) {
            task.progressCallback()?.onFail(ERROR_TAG_7, task)
            return task
        }
        // 校验请求地址是否正在下载
        if (taskState.exitRunningUrl(task.url())) {
            task.progressCallback()?.onFail(ERROR_TAG_8, task)
            return task
        }
        // 校验请求地址是否已经在任务队列
        if (taskState.exitWaitUrl(task.url())) {
            task.progressCallback()?.onFail(ERROR_TAG_10, task)
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