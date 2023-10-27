package com.itg.net.download.data

import android.app.Activity
import androidx.fragment.app.FragmentActivity
import com.itg.net.DdNet
import com.itg.net.download.*
import com.itg.net.download.TaskCallbackMgr
import com.itg.net.download.interfaces.IProgressCallback
import com.itg.net.download.operations.DownloadEndNotify
import com.itg.net.download.operations.PrincipalLife

class TaskBuilder {
    private val task by lazy { Task() }
    private val iProgressCallback by lazy {
        object : IProgressCallback {
            override fun onConnecting(task: Task) {
                DownloadEndNotify.connectNotify(task)
            }

            override fun onProgress(task: Task, complete:Boolean) {
                if (complete) {
                    DownloadEndNotify.completeNotify(task)
                } else {
                    DownloadEndNotify.progressNotify(task)
                }
            }

            override fun onFail(error: String?, task: Task) {
                DownloadEndNotify.failNotify(task, error)
            }

        }
    }

    // 持有activity引用的回调
    private var holdActivityRef: IProgressCallback? = null

    fun path(path: String): TaskBuilder {
        task.path = path
        return this
    }

    fun url(url: String): TaskBuilder {
        task.url = url
        return this
    }

    fun tryAgainCount(count: Int): TaskBuilder {
        task.tryAgainCount = count
        return this
    }

    //自动移除持有activity引用的回调
    fun autoRemote(activity: FragmentActivity): TaskBuilder {
        PrincipalLife.observeActivityLife(task, activity)
        return this
    }

    fun setDownloadListener(progressBack: IProgressCallback): TaskBuilder {
        holdActivityRef = progressBack
        TaskCallbackMgr.instance.setProgressCallback(task, progressBack)
        return this
    }

    fun stealth(): TaskBuilder {
        return this
    }


    fun start(): Task {
        val taskState = DdNet.instance.download.dispatchTool.getTaskState()
        // 校验任务是否为无效任务
        if (taskState.isInvalidTask(task)) {
            holdActivityRef?.onFail(ERROR_TAG_7, task)
            PrincipalLife.removeProgressCallback(task)
            return task
        }
        // 校验请求地址是否正在下载
        if (taskState.exitRunningUrl(task.url)) {
            holdActivityRef?.onFail(ERROR_TAG_8, task)
            PrincipalLife.removeProgressCallback(task)
            return task
        }
        // 校验请求地址是否已经在任务队列
        if (taskState.exitWaitUrl(task.url)) {
            holdActivityRef?.onFail(ERROR_TAG_10, task)
            PrincipalLife.removeProgressCallback(task)
            return task
        }
        // 下载任务是否启动断点续传
        if (taskState.isBreakpointContinuation(task)) {
            task.iProgressCallback = iProgressCallback
            DdNet.instance.download.dispatchTool.appendDownload(task)
            return task
        }
        task.iProgressCallback = iProgressCallback
        DdNet.instance.download.dispatchTool.download(task)
        return task
    }

}