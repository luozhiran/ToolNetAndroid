package com.itg.net.download.request

import android.util.Log
import com.itg.net.DdNet
import com.itg.net.download.DEBUG_TAG
import com.itg.net.download.Task
import com.itg.net.download.data.LockData
import com.itg.net.download.interfaces.ITask
import com.itg.net.download.operations.PrincipalLife

class TaskState {

    private val maxDownloadSize = DdNet.instance.ddNetConfig.maxDownloadNum

    private val runningTaskLock by lazy { LockData() }
    private val waitTaskLock by lazy { LockData() }

    //队列下载任务
    private val mWaitTask: MutableList<Task> by lazy { mutableListOf() }
    private val mWaitQueueUrl: MutableList<String?> by lazy { mutableListOf() }

    //正在执行任务
    private val mRunningTasks: MutableList<Task> by lazy { mutableListOf() }
    private val mRunningTasksUrl: MutableList<String?> by lazy { mutableListOf() }


    fun addWaitTask(task: Task):Boolean {
        synchronized(waitTaskLock) {
            if (exitWaitUrl(task.url)) {
                // 添加下载任务失败时，需要删除创建任务时生成的全局变量
                PrincipalLife.removeProgressCallback(task as? Task)
                return false
            }
            if (mWaitTask.add(task)) {
                mWaitQueueUrl.add(task.url)
                return true
            }
        }
        // 添加下载任务失败时，需要删除创建任务时生成的全局变量
        PrincipalLife.removeProgressCallback(task as? Task)
        return false
    }

    fun deleteWaitTask(task: Task?) {
        if (task == null) return
        synchronized(waitTaskLock) {
            if (mWaitTask.remove(task)) {
                mWaitQueueUrl.remove(task.url)
            }
            PrincipalLife.removeProgressCallback(task)
        }
    }

    fun deleteWaitTask(url: String?) {
        if (!quickDeleteWaitTask(url)) {
            synchronized(waitTaskLock) {
                val iterator = mWaitTask.iterator()
                var item: Task? = null
                while (iterator.hasNext()) {
                    item = iterator.next()
                    if (item.url == url) {
                        iterator.remove()
                        mWaitQueueUrl.remove(url)
                        PrincipalLife.removeProgressCallback(item)
                        break
                    }
                }
            }
        }
    }

    private fun quickDeleteWaitTask(url: String?): Boolean {
        if (url.isNullOrBlank()) return false
        synchronized(waitTaskLock) {
            val position = mWaitQueueUrl.indexOf(url)
            if (position > 0 && position < mWaitTask.size) {
                if (mWaitTask[position].url == url) {
                    mWaitQueueUrl.removeAt(position)
                    val task = mWaitTask.removeAt(position)
                    PrincipalLife.removeProgressCallback(task)
                    return true
                }
            }
        }
        return false
    }

    private fun findFirstTaskFromWaitQueue(): Task? {
        if (mWaitTask.size > 0) {
            mWaitQueueUrl.removeAt(0)
            return mWaitTask.removeAt(0)
        }
        return null
    }

    fun addRunningTask(task: Task?): Boolean {
        if (task == null) return false
        synchronized(runningTaskLock) {
            if (mRunningTasksUrl.contains(task.url)) {
                // 添加下载任务失败时，需要删除创建任务时生成的全局变量
                PrincipalLife.removeProgressCallback(task)
                return false
            }
            if (mRunningTasks.add(task)) {
                mRunningTasksUrl.add(task.url)
                return true
            }
        }
        // 添加下载任务失败时，需要删除创建任务时生成的全局变量
        PrincipalLife.removeProgressCallback(task as? Task)
        return false
    }

    fun deleteRunningTask(task: Task?) {
        if (task == null) return
        synchronized(runningTaskLock) {
            if (mRunningTasks.remove(task)) {
                mRunningTasksUrl.remove(task.url)
            }
        }
        PrincipalLife.removeProgressCallback(task)
    }

    private fun quickDeleteRunningTask(url: String?): Boolean {
        if (url.isNullOrBlank()) return false
        synchronized(runningTaskLock) {
            val position = mRunningTasksUrl.indexOf(url)
            if (position > 0 && position < mRunningTasks.size) {
                if (mRunningTasks[position].url == url) {
                    mRunningTasksUrl.removeAt(position)
                    val task = mRunningTasks.removeAt(position)
                    PrincipalLife.removeProgressCallback(task)
                    return true
                }
            }
        }
        return false
    }

    fun deleteRunningTask(url: String?) {
        if (!quickDeleteRunningTask(url)) {
            synchronized(runningTaskLock) {
                val iterator = mRunningTasks.iterator()
                var item: Task? = null
                while (iterator.hasNext()) {
                    item = iterator.next()
                    if (item.url == url) {
                        iterator.remove()
                        mRunningTasksUrl.remove(url)
                        PrincipalLife.removeProgressCallback(item)
                        break
                    }
                }
            }
        }
    }

    fun exitRunningTask(task: Task?): Boolean {
        if (task == null) return false
        return mRunningTasks.contains(task)
    }

    fun exitWaitTask(task: Task?): Boolean {
        if (task == null) return false
        return mWaitTask.contains(task)
    }

    fun exitRunningUrl(url: String?): Boolean {
        if (url.isNullOrBlank()) return false
        return mRunningTasksUrl.contains(url)
    }

    fun exitWaitUrl(url: String?): Boolean {
        if (url.isNullOrBlank()) return false
        return mWaitQueueUrl.contains(url)
    }

    /**
     * 校验是否是无效下载任务
     * @param task DTask?
     * @return Boolean
     */
    fun isInvalidTask(task: Task?): Boolean {
        if (task == null) return true
        if (task.url.isNullOrBlank()) return true
        if (task.url == task.cancelUrl) return true
        return false
    }

    /**
     * 断点续传
     * @param task Task
     * @return Boolean
     */
    fun isBreakpointContinuation(task: Task): Boolean {
        return task.append
    }


    /**
     * 下载队列是否可以接收新的下载任务
     * @return Boolean
     */
    fun runningQueueCanAcceptTask(): Boolean {
        return mRunningTasks.size < maxDownloadSize;
    }

    /**
     * 按顺序从等待队列中取出下载任务
     * @param task Task
     */
    fun getTaskFromWaitQueue(task: Task?): Task? {
        return if (task == null) {
            findFirstTaskFromWaitQueue()
        } else {
            addWaitTask(task)
            findFirstTaskFromWaitQueue()
        }
    }

    /**
     * 是否需要检测md5
     * @param task Task
     * @return Boolean
     */
    fun isCheckMd5(task: Task): Boolean {
        return task.md5.orEmpty().isNotBlank()
    }

    fun canNextTask(): Boolean {
        if (runningQueueCanAcceptTask() && mWaitTask.size > 0) return true
        return false
    }

    fun debugPrint(){
        Log.i(DEBUG_TAG,"下载队列： 等待任务队列：${mWaitTask.size}，正在下载队列：${mRunningTasks.size}")
    }
}