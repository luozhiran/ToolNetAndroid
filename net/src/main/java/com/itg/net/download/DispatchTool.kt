package com.itg.net.download

import android.os.HandlerThread
import android.os.Looper
import android.os.Message
import android.text.TextUtils
import android.util.Log
import com.itg.net.DdNet
import com.itg.net.ModeType
import com.itg.net.download.interfaces.Dispatch
import com.itg.net.download.interfaces.IProgressCallback
import com.itg.net.download.interfaces.Task
import com.itg.net.reqeust.model.params.ParamsBuilder
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.*
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class DispatchTool : Dispatch {
    //队列下载任务
    private val mTaskQueue: MutableList<Task> = mutableListOf()
    private val mTaskQueueUrl: MutableList<String?> = mutableListOf()

    //正在执行任务
    private val mRunningTasks: MutableList<Task> = mutableListOf()
    private val mRunningTasksUrl: MutableList<String?> = mutableListOf()

    @Volatile
    private var looper: Looper? = null

    @Volatile
    private var handler: ReceiverHandler? = null

    private val lock = this


    init {
        val thread = HandlerThread("ddl")
        thread.start()
        looper = thread.looper
        handler = ReceiverHandler(looper!!) {
            synchronized(lock) {
                if (it.what == DOWNLOAD_FILE) {
                    val preTask = it.obj as? TempTask?
                    if (preTask?.task == null) {
                        downloadNextTask()
                    } else {
                        if (preTask.task.tryAgainCount() <= 0) {
                            downloadNextTask()
                        } else {
                            tryAgainDownloadTask(preTask.task, preTask.iProgressCallback)
                        }
                    }
                } else { // DOWNLOAD_TASK CANCEL_TASK DOWNLOAD_FILE  DOWNLOAD_SUCCESS
                    downloadNextTask()
                }

            }
            true
        }
    }


    /**
     * 重任务队列中，获取新任务并下载该任务
     */
    private fun downloadNextTask() {
        synchronized(lock) {
            if (mTaskQueue.size > 0 && mRunningTasks.size <= DdNet.instance.ddNetConfig.maxDownloadNum) {
                if (mTaskQueue.isNotEmpty() && mTaskQueueUrl.isNotEmpty()) {
                    val task = mTaskQueue.removeFirst()
                    mTaskQueueUrl.removeFirst()
                    if (task.append()) {
                        appendDownload(task as DTask)
                    } else {
                        download(task as DTask)
                    }
                }

            }
        }
    }

    /**
     * 任务有重试次数，在一次上次失败的任务
     */
    private fun tryAgainDownloadTask(preTask: DTask, progressCallback: IProgressCallback?) {
        synchronized(lock) {
            if (mRunningTasks.contains(preTask)) {
                progressCallback?.let {
                    //把外部的回调还原给任务
                    TaskCallbackMgr.instance.setProgressCallback(preTask, it)
                }
                if (mRunningTasks.size <= DdNet.instance.ddNetConfig.maxDownloadNum) {
                    if (preTask.append()) {
                        appendDownload(preTask, true)
                    } else {
                        download(preTask, true)
                    }
                }
            } else {
                downloadNextTask()
            }
        }
    }

    override fun download(task: DTask) {
        download(task, false)
    }

    /**
     *
     * @param task DTask
     * @param tryAgainDownload Boolean 重试下载任务
     */
    private fun download(task: DTask, tryAgainDownload: Boolean = false) {
        if (tryAgainDownload ||downloadQueueNoTask(task)) {
            task.tryAgainCount(task.tryAgainCount() - 1)
            task.progressCallback()?.onConnecting(task)
            sendDownloadRequest(task, null) { type, tag ->
                handleResult(task, type, tag)
            }
        } else {
            needSendDownloadRequest()
        }
    }

    override fun appendDownload(task: DTask) {
        appendDownload(task, false)
    }

    /**
     *
     * @param task DTask
     * @param tryAgainDownload Boolean 重试下载任务
     */
    private fun appendDownload(task: DTask, tryAgainDownload: Boolean = false) {
        if (tryAgainDownload || downloadQueueNoTask(task)) {
            task.tryAgainCount(task.tryAgainCount() - 1)
            task.progressCallback()?.onConnecting(task)
            getBuilder(task, null).send(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    handleResult(task, DOWNLOAD_FILE, e.message.toString())
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.code == 200) {
                        val file = File(task.path() + ".tmp")
                        if (file.exists()) {
                            sendDownloadRequest(
                                task,
                                "${file.length()}-${response.body?.contentLength() ?: 0 - 1}"
                            ) { type, tag ->
                                handleResult(task, type, tag)
                            }
                        } else {
                            sendDownloadRequest(
                                task,
                                "${0}-${response.body?.contentLength() ?: 0 - 1}"
                            ) { type, tag ->
                                handleResult(task, type, tag)
                            }
                        }
                    } else {
                        handleResult(task, DOWNLOAD_FILE, "请求失败：response.code=${response.code}")
                    }
                }

            }) { call: Call? ->
                (task as? BusinessTask)?.registerEvent(call, task)
            }
        } else {
            needSendDownloadRequest()
        }
    }

    @Synchronized
    fun taskQueueHasTask(task: DTask): Boolean {
        return mTaskQueueUrl.contains(task.url())
    }

    @Synchronized
    fun runningQueueHasTask(task: DTask): Boolean {
        return mRunningTasksUrl.contains(task.url())
    }


    private fun needSendDownloadRequest() {
        if (mRunningTasksUrl.size == 0 && mTaskQueueUrl.size > 0) {
            sendMsg(null, DOWNLOAD_TASK)
        }
    }


    @Synchronized
    fun putTaskToRunning(task: DTask, deleteTaskQueue: Boolean = false): Boolean {
        return if (mRunningTasks.size < DdNet.instance.ddNetConfig.maxDownloadNum) {
           val runningTaskSuc = mRunningTasks.add(task)
            val runningTaskUrlSuc = mRunningTasksUrl.add(task.url()!!)
            if (deleteTaskQueue) {
                mTaskQueue.remove(task)
                mTaskQueueUrl.remove(task.url())
            }
            runningTaskSuc && runningTaskUrlSuc
        } else {
            false
        }
    }

    @Synchronized
    fun putTaskToQueue(task: DTask) {
        mTaskQueue.add(task)
        mTaskQueueUrl.add(task.url())
    }


    /**
     * 一个下载url只能下载一次；
     * 如果下载缓存队列或者正在下载的队列存在下载任务，则不在惊险下载
     * @param task DTask
     * @return Boolean
     */
    @Synchronized
    fun downloadQueueNoTask(task: DTask): Boolean {
        if (task.url().equals(task.cancel())) {
            sendMsg(TempTask(task, null), CANCEL_TASK)
            return false
        }
        if (!taskQueueHasTask(task) && !runningQueueHasTask(task)) {
            if (putTaskToRunning(task)) return true
        }

        if (taskQueueHasTask(task) && !runningQueueHasTask(task)) {
            if (putTaskToRunning(task, true)) return true
        }
        if (runningQueueHasTask(task)) return false
        if (!taskQueueHasTask(task)) {
            putTaskToQueue(task)
            return false
        }
//        task.progressCallback()?.onFail(ERROR_TAG_10, task)
        return false
    }


    fun sendDownloadRequest(
        task: DTask,
        header: String?,
        callback: (type: Int, tag: String) -> Unit
    ) {
        getBuilder(task, header).send(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                handleResult(task, DOWNLOAD_FILE, e.message.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                if (task.append()) {
                    if (response.code == 206) {
                        handleResponse(response, task, callback)
                    } else {
                        handleResult(task, DOWNLOAD_FILE, ERROR_TAG_1)
                    }
                } else {
                    if (response.code == 200) {
                        handleResponse(response, task, callback)
                    } else {
                        handleResult(task, DOWNLOAD_FILE, ERROR_TAG_9)
                    }
                }
            }
        }) { call: Call? ->
            (task as? BusinessTask)?.registerEvent(call, task)
        }
    }

    private fun handleResult(task: DTask, type: Int, tag: String) {
        var tempOuterIProgressCallback: IProgressCallback? = null
        var noNeedDeleteRunningQuent = true
        var tempTag = tag
        if (type == DOWNLOAD_FILE) {
            if (task.tryAgainCount() > 0) {
                //如果重试次数没有用完，需要保存一个回到最外层的应用，在重试下载开始后，置空这个回调
                noNeedDeleteRunningQuent = false
                tempOuterIProgressCallback = TaskCallbackMgr.instance.getProgressCallback(task)
                // 这里拦截tag，使用 ERROR_TAG_11 替换
                tempTag = ERROR_TAG_11
            }
            task.progressCallback()?.onFail(tempTag, task)
        } else if (type == DOWNLOAD_SUCCESS) {
            task.progressCallback()?.onProgress(task)
        }
        synchronized(lock) {
            //重试下载时，不需要删除现在队列中的任务，会继续下载任务
            if (noNeedDeleteRunningQuent) {
                mRunningTasksUrl.remove(task.url())
                mRunningTasks.remove(task)
            }
        }
        sendMsg(TempTask(task, tempOuterIProgressCallback), type)
    }

    private fun getBuilder(task: DTask, header: String?): ParamsBuilder {
        val builder = DdNet.instance.builder(ModeType.Get).url(task.url())
        header?.apply { builder.addHeader("RANGE", "bytes=$header") }
        task.param?.onEach { value ->
            builder.addHeader(value.key, value.value)
        }
        return builder
    }

    private fun handleResponse(
        response: Response,
        task: DTask,
        callback: (type: Int, tag: String) -> Unit
    ) {
        val file = File(task.path() + ".tmp")
        if (task.append()) {
            task.setContentLength(response.body?.contentLength() ?: 0 + file.length())
        } else {
            task.setContentLength(response.body?.contentLength() ?: 0)
        }
        var mkSuccess = true
        if (file.parentFile != null && !file.parentFile!!.exists()) {
            mkSuccess = file.parentFile!!.mkdirs()
        }
        if (mkSuccess) {
            if (response.body == null) {
                callback.invoke(DOWNLOAD_FILE, ERROR_TAG_6)
            } else {
                saveNetStream(response.body!!.byteStream(), file, task, callback)
            }
            response.body?.close()
        } else {
            callback.invoke(DOWNLOAD_FILE, ERROR_TAG_2)
            response.body?.close()
        }
    }


    private fun saveNetStream(
        inputStream: InputStream,
        file: File,
        task: DTask,
        callback: (type: Int, tag: String) -> Unit
    ) {
        val buffer = ByteArray(1024 shl 2)
        var length = -1
        var out: OutputStream? = null
        try {
            out = FileOutputStream(file, task.append())
            var pre = 0
            var cur = 0
            while (inputStream.read(buffer).also { length = it } > 0) {
                out.write(buffer, 0, length)
                task.setDownloadSize(file.length())
                cur = task.getProgress()
                if (!TextUtils.isEmpty(task.cancel()) && task.url().equals(task.cancel())) {
                    if (!task.append()) { //如果不支持断点续传，则取消任务时删除下载的部分数据
                        File(task.path().toString() + ".tmp").delete()
                    }
                    callback.invoke(DOWNLOAD_FILE, ERROR_TAG_3)
                    return
                } else {
                    if (cur != pre) {
                        if (cur == 100) {
                            if (needCheckMd5(task)) {
                                if (!checkMd5(task.md5(), file.absolutePath)) {
                                    File(task.path().toString() + ".tmp").delete()
                                    callback.invoke(DOWNLOAD_FILE, ERROR_TAG_4)
                                    return
                                }
                            }
                            val distFile = File(file.absolutePath.replace(".tmp", ""))
                            val renameSuccess = file.renameTo(distFile)
                            if (renameSuccess) {
                                callback.invoke(DOWNLOAD_SUCCESS, "任务下载成功")
                            } else {
                                callback.invoke(DOWNLOAD_FILE, ERROR_TAG_5)
                            }
                        } else {
                            task.progressCallback()?.onProgress(task)
                        }
                    }
                }
                pre = cur
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            callback.invoke(DOWNLOAD_FILE, ERROR_TAG_7)
        } catch (e: IOException) {
            e.printStackTrace()
            callback.invoke(DOWNLOAD_FILE, ERROR_TAG_8)
        } finally {
            try {
                out?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun cancelTask(url: String?) {
        var needDeleteTask: Task? = null
        mTaskQueue.forEach {
            if (it.url().equals(url)) {
                it.cancel(url)
                needDeleteTask = it
                return@forEach
            }
        }
        synchronized(lock) {
            if (needDeleteTask != null) {
                mTaskQueue.remove(needDeleteTask)
                mTaskQueueUrl.remove(needDeleteTask!!.url())
                return
            }
        }
        mRunningTasks.forEach {
            if (it.url().equals(url)) {
                it.cancel(url)
                needDeleteTask = it
                return@forEach
            }
        }
        synchronized(lock) {
            if (needDeleteTask != null) {
                mRunningTasks.remove(needDeleteTask)
                mRunningTasksUrl.remove(needDeleteTask!!.url())
                DdNet.instance.cancelFirstTag(url)
            }
        }
    }

    fun cancelTask(task: Task?) {
        if (task == null) return
        task.cancel(task.url())
        synchronized(lock) {
            mTaskQueue.remove(task)
            mTaskQueueUrl.remove(task.url())
            mRunningTasks.remove(task).let {
                if (it) {
                    DdNet.instance.cancelFirstTag(task.url())
                }
            }
            mRunningTasksUrl.remove(task.url())
        }
    }

    fun getTask(url: String?): Task? {
        mTaskQueue.forEach {
            if (it.url().equals(url)) {
                return it
            }
        }

        mRunningTasks.forEach {
            if (it.url().equals(url)) {
                return it
            }
        }
        return null
    }

    fun isQueue(url: String?): Boolean {
        synchronized(lock) {
            return mRunningTasksUrl.contains(url) || mTaskQueueUrl.contains(url)
        }
    }

    private fun sendMsg(task: TempTask?, type: Int) {
        val msg = Message.obtain()
        msg.obj = task
        msg.what = type
        handler?.sendMessage(msg)
    }


    private fun getMD5Three(path: String?): String? {
        var bi: BigInteger? = null
        try {
            val buffer = ByteArray(8192)
            var len = 0
            val md: MessageDigest = MessageDigest.getInstance("MD5")
            val f = File(path)
            val fis = FileInputStream(f)
            while (fis.read(buffer).also { len = it } != -1) {
                md.update(buffer, 0, len)
            }
            fis.close()
            val b: ByteArray = md.digest()
            bi = BigInteger(1, b)
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return bi?.toString(16)
    }


    private fun needCheckMd5(task: DTask?): Boolean {
        val md5 = task?.md5()
        if (md5.orEmpty().isNotBlank()) {
            return true
        }
        return false
    }

    private fun checkMd5(targetMd5: String?, path: String?): Boolean {
        if (targetMd5.orEmpty().isNotBlank()) {
            val downloadFileMd5 = getMD5Three(path)
            return downloadFileMd5 == targetMd5
        }
        return false
    }
}