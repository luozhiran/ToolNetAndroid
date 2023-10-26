package com.itg.net.download.data

import com.itg.net.DdNet
import com.itg.net.download.Task
import com.itg.net.download.interfaces.ITask

class TaskState {

    private val maxDownloadSize = DdNet.instance.ddNetConfig.maxDownloadNum

    private val runningTaskLock by lazy { LockData() }
    private val waitTaskLock by lazy { LockData() }

    //队列下载任务
    private val mWaitTask: MutableList<ITask> by lazy { mutableListOf() }
    private val mWaitQueueUrl: MutableList<String?> by lazy { mutableListOf() }

    //正在执行任务
    private val mRunningTasks: MutableList<ITask> by lazy { mutableListOf() }
    private val mRunningTasksUrl: MutableList<String?> by lazy { mutableListOf() }


    private fun addWaitTask(task: ITask?) {
        if (task == null) return
        if (exitWaitUrl(task.url())) return
        if (mWaitTask.add(task)) {
            mWaitQueueUrl.add(task.url())
        }
    }

    fun deleteWaitTask(task: ITask?) {
        if (task == null) return
        synchronized(waitTaskLock) {
            if (mWaitTask.remove(task)) {
                mWaitQueueUrl.remove(task.url())
            }
        }
    }

    fun deleteWaitTask(url: String?) {
        if (!quickDeleteWaitTask(url)) {
            synchronized(waitTaskLock) {
                val iterator = mWaitTask.iterator()
                var item: ITask? = null
                while (iterator.hasNext()) {
                    item = iterator.next()
                    if (item.url() == url) {
                        iterator.remove()
                        mWaitQueueUrl.remove(url)
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
                if (mWaitTask[position].url() == url) {
                    mWaitQueueUrl.removeAt(position)
                    mRunningTasks.removeAt(position)
                    return true
                }
            }
        }
        return false
    }

    private fun findFirstTaskFromWaitQueue(): ITask? {
        if (mWaitTask.size > 0) {
            mWaitQueueUrl.removeAt(0)
            return mWaitTask.removeAt(0)
        }
        return null
    }

    fun addRunningTask(task: ITask?): Boolean {
        if (task == null) return false
        synchronized(runningTaskLock) {
            if (mRunningTasksUrl.contains(task.url())) return false
            if (mRunningTasks.add(task)) {
                mRunningTasksUrl.add(task.url())
                return true
            }
        }
        return false
    }

    fun deleteRunningTask(task: ITask?) {
        if (task == null) return
        synchronized(runningTaskLock) {
            if (mRunningTasks.remove(task)) {
                mRunningTasksUrl.remove(task.url())
            }

        }
    }

    private fun quickDeleteRunningTask(url: String?): Boolean {
        if (url.isNullOrBlank()) return false
        synchronized(runningTaskLock) {
            val position = mRunningTasksUrl.indexOf(url)
            if (position > 0 && position < mRunningTasks.size) {
                if (mRunningTasks[position].url() == url) {
                    mRunningTasksUrl.removeAt(position)
                    mRunningTasks.removeAt(position)
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
                var item: ITask? = null
                while (iterator.hasNext()) {
                    item = iterator.next()
                    if (item.url() == url) {
                        iterator.remove()
                        mRunningTasksUrl.remove(url)
                        break
                    }
                }
            }
        }
    }

    fun exitRunningTask(task: ITask?): Boolean {
        if (task == null) return false
        return mRunningTasks.contains(task)
    }

    fun exitWaitTask(task: ITask?): Boolean {
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
        if (task.url().isNullOrBlank()) return true
        if (task.url() == task.cancel()) return true
        return false
    }

    /**
     * 断点续传
     * @param task Task
     * @return Boolean
     */
    fun isBreakpointContinuation(task: ITask): Boolean {
        return task.append()
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
    fun getTaskFromWaitQueue(task: ITask?): ITask? {
        synchronized(waitTaskLock) {
            if (mWaitTask.size > 0) {
                val resultTask = findFirstTaskFromWaitQueue() ?: return task
                addWaitTask(task)
                return resultTask
            }
        }
        return task
    }

    /**
     * 是否需要检测md5
     * @param task Task
     * @return Boolean
     */
    fun isCheckMd5(task: ITask): Boolean {
        return task.md5().orEmpty().isNotBlank()
    }

    fun canNextTask(): Boolean {
        if (runningQueueCanAcceptTask() && mWaitTask.size > 0) return true
        return false
    }

    fun downloadComplete(task: ITask): Boolean {
        return task.getProgress() != 100
    }
}