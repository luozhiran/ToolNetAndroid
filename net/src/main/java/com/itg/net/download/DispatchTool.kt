package com.itg.net.download

import android.os.HandlerThread
import android.os.Looper
import android.os.Message
import com.itg.net.download.data.TaskState
import com.itg.net.download.interfaces.Dispatch
import com.itg.net.download.interfaces.IProgressCallback
import com.itg.net.download.request.BreakpointContinuationRequest
import com.itg.net.download.request.DirectRequest


class DispatchTool : Dispatch {

    private val taskStateInstance by lazy { TaskState() }

    @Volatile
    private var looper: Looper? = null

    @Volatile
    private var handler: ReceiverHandler? = null

    init {
        val thread = HandlerThread("ddl")
        thread.start()
        looper = thread.looper
        handler = ReceiverHandler(looper!!) { execNextDownloadRequest(it) }
    }

    /**
     * 从任务队列中获取下载任务
     */
    @Synchronized
    private fun downloadNextTask() {
        if (taskStateInstance.runningQueueCanAcceptTask()) {
            taskStateInstance.getTaskFromWaitQueue(null)?.apply {
                if (taskStateInstance.addRunningTask(this)) {
                    if (taskStateInstance.isBreakpointContinuation(this)) {
                        appendDownload(this as Task)
                    } else {
                        download(this as Task)
                    }
                } else {
                    downloadNextTask()
                }
            }
        }
    }

    /**
     * 任务有重试次数，在一次上次失败的任务
     */
    private fun tryAgainDownloadTask(preTask: Task, progressCallback: IProgressCallback?) {
        synchronized(this) {
            if (taskStateInstance.exitRunningTask(preTask)) {
                progressCallback?.let {
                    //把外部的回调还原给任务
                    TaskCallbackMgr.instance.setProgressCallback(preTask, it)
                }
                if (taskStateInstance.isBreakpointContinuation(preTask)) {
                    logisticsBreakpointContinuation(preTask)
                } else {
                    logisticsDownload(preTask)
                }
            } else {
                downloadNextTask()
            }
        }
    }

    override fun download(task: Task) {
        synchronized(this) {
            if (taskStateInstance.runningQueueCanAcceptTask()) {
                immediatelyDownload(task)
            } else {
                pendingDownload()
            }
        }
    }


    /**
     * 立刻下载数据
     * @param task DTask
     */
    private fun immediatelyDownload(task: Task) {
        // 从等待队列中取得下载任务
        val downloadTask = taskStateInstance.getTaskFromWaitQueue(task) as Task
        // 把任务存储到下载队列
        taskStateInstance.addRunningTask(downloadTask)
        // 开始下载
        logisticsDownload(downloadTask)
    }

    /**
     * 转发下载
     * @param task DTask
     */
    private fun logisticsDownload(task: Task) {
        task.tryAgainCount(task.tryAgainCount() - 1)
        task.progressCallback()?.onConnecting(task)
        DirectRequest(task, taskStateInstance)
            .setFailCallback { tk, msg -> handleResult(tk as Task, DOWNLOAD_FILE, msg) }
            .setSuccessCallback { tk, msg -> handleResult(tk as Task, DOWNLOAD_SUCCESS, msg) }
            .start()
    }

    /**
     * 等待未来下载数据
     */
    private fun pendingDownload() {
        if (taskStateInstance.canNextTask()) {
            sendMsg(null, DOWNLOAD_TASK)
        }
    }

    /**
     * 断点续传下载
     * @param task DTask
     */
    override fun appendDownload(task: Task) {
        synchronized(this) {
            if (taskStateInstance.runningQueueCanAcceptTask()) {
                immediatelyBreakpointContinuationRequest(task)
            } else {
                pendingDownload()
            }
        }
    }

    /**
     * 发起断点位置请求
     * @param task DTask
     */
    private fun immediatelyBreakpointContinuationRequest(task: Task) {
        // 从等待队列中取得下载任务
        val downloadTask = taskStateInstance.getTaskFromWaitQueue(task) as Task
        // 把任务存储到下载队列
        taskStateInstance.addRunningTask(downloadTask)
        logisticsBreakpointContinuation(task)
    }

    /**
     * 转发断点续传
     * @param task DTask
     */
    private fun logisticsBreakpointContinuation(task: Task){
        task.tryAgainCount(task.tryAgainCount() - 1)
        task.progressCallback()?.onConnecting(task)
        BreakpointContinuationRequest(task, taskStateInstance)
            .setFailCallback { tk, msg -> handleResult(tk as Task, DOWNLOAD_FILE, msg) }
            .setSuccessCallback { tk, msg -> handleResult(tk as Task, DOWNLOAD_SUCCESS, msg) }
            .start()
    }


    private fun handleResult(task: Task, type: Int, tag: String) {
        var tempTag = tag
        var tempTask:TempTask?=null
        if (type == DOWNLOAD_FILE) {
            if (task.tryAgainCount() > 0) {
                //如果重试次数没有用完，需要保存一个回到最外层的应用，在重试下载开始后，置空这个回调
                tempTask = TempTask(task, TaskCallbackMgr.instance.getProgressCallback(task))
                task.progressCallback()?.onFail(ERROR_TAG_11, task)
            } else {
                tempTask = TempTask()
                taskStateInstance.deleteRunningTask(task)
                task.progressCallback()?.onFail(tempTag, task)
            }
        } else if (type == DOWNLOAD_SUCCESS) {
            tempTask = TempTask()
            taskStateInstance.deleteRunningTask(task)
            task.progressCallback()?.onProgress(task)
        }
        sendMsg(tempTask, type)
    }

    private fun sendMsg(task: TempTask?, type: Int) {
        val msg = Message.obtain()
        msg.obj = task
        msg.what = type
        handler?.sendMessage(msg)
    }

    private fun execNextDownloadRequest(message: Message): Boolean {
        synchronized(this) {
            if (message.what == DOWNLOAD_FILE) {
                handleDownloadFail(message.obj as? TempTask?)
            } else { // DOWNLOAD_TASK CANCEL_TASK DOWNLOAD_FILE  DOWNLOAD_SUCCESS
                downloadNextTask()
            }

        }
        return true
    }

    private fun handleDownloadFail(task: TempTask?) {
        if (task?.task == null) {
            downloadNextTask()
            return
        }
        val realTask = task.task
        val realProgressCallback = task.iProgressCallback
        if (realTask.tryAgainCount() <= 0) {
            downloadNextTask()
        } else { // 重试下载
            tryAgainDownloadTask(realTask, realProgressCallback)
        }
    }

    fun getTaskState(): TaskState {
        return taskStateInstance
    }
}