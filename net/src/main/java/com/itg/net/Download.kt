package com.itg.net

import com.itg.net.download.BusinessTask
import com.itg.net.download.Task
import com.itg.net.download.DispatchTool
import com.itg.net.download.TaskCallbackMgr
import com.itg.net.download.interfaces.IProgressCallback
import com.itg.net.download.interfaces.ITask

class Download {

    val dispatchTool: DispatchTool by lazy { DispatchTool() }

    fun downloadTask(): Task {
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


    /**
     * 启动下载请求时，没有调用autoCancel()方法且不取消下载任务后台继续保持下载时， 必须手动取消内部下载监听器，否则会导致内存泄露
     * 或者调用DdNet.instance.download.cancel(task),取消任务同时会释放下载器
     * @param task Task?
     */
    fun removeInnerProgressListener(task: ITask?){
        if (task is Task) {
            TaskCallbackMgr.instance.removeProgressCallback(task)
        }
    }

    fun isQueue(url: String): Boolean {
        val taskState = dispatchTool.getTaskState()
        return taskState.exitWaitUrl(url) || taskState.exitRunningUrl(url)
    }

    fun cancel(url: String?) {
        val taskState = dispatchTool.getTaskState()
        if (taskState.exitRunningUrl(url)) {
            taskState.deleteRunningTask(url)
            DdNet.instance.cancelFirstTag(url)
        }else if (taskState.exitWaitUrl(url)) {
            taskState.deleteWaitTask(url)
        }
    }

    fun cancel(task: ITask?) {
        val taskState = dispatchTool.getTaskState()
        if (taskState.exitRunningTask(task)) {
            taskState.deleteRunningTask(task)
            DdNet.instance.cancelFirstTag(task?.url())
        } else if (taskState.exitWaitTask(task)) {
            taskState.deleteWaitTask(task)
        }
    }

}