package com.itg.net.download

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Message
import android.text.TextUtils
import com.itg.net.DdNet
import com.itg.net.base.Builder
import com.itg.net.download.interfaces.Dispatch
import com.itg.net.download.interfaces.Task
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.*


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
        handler = ReceiverHandler(looper!!, object : Handler.Callback {
            override fun handleMessage(msg: Message): Boolean {
                synchronized(this) {
                    if (mTaskQueue.size > 0 && mRunningTasks.size <= DdNet.instance.ddNetConfig.maxDownloadNum) {
                        val task = mTaskQueue.removeLast()
                        mTaskQueueUrl.removeLast()
                        if (task.append()) {
                            appendDownload(task as DTask)
                        } else {
                            download(task as DTask)
                        }
                    }
                }
                return true
            }
        })
    }

    override fun download(task: DTask) {
        task.progressCallback()?.onConnecting(task)
        if (isDownload(task)) {
           sendDownloadRequest(task,null){type, tag ->
               handleResult(task, type, tag)
           }
        } else {
            if (mTaskQueue.size > 0) {
                handler?.sendEmptyMessage(DOWNLOAD_TASK)
            }
        }
    }

    override fun appendDownload(task: DTask) {
        if (isDownload(task)) {
            task.progressCallback()?.onConnecting(task)
            getBuilder(task,null).send(object : Callback{
                override fun onFailure(call: Call, e: IOException) {
                    handleResult(task, DOWNLOAD_FILE, e.message.toString())
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.code == 200) {
                        val file = File(task.path()+".tmp")
                        if (file.exists()) {
                            sendDownloadRequest(task,"${file.length()}-${response.body?.contentLength()?:0-1}"){type, tag ->
                                handleResult(task, type, tag)
                            }
                        } else {
                            sendDownloadRequest(task,"${0}-${response.body?.contentLength()?:0-1}"){type, tag ->
                                handleResult(task, type, tag)
                            }
                        }
                    } else {
                        handleResult(task, DOWNLOAD_FILE, "请求失败：response.code=${response.code}")
                    }
                }

            })
        }
    }

    @Synchronized
    fun taskQueueHasTask(task: DTask): Boolean {
        val hasUrl = mTaskQueueUrl.contains(task.url())
        val hasTask = mTaskQueue.contains(task)
        return hasUrl && hasTask
    }

    @Synchronized
    fun runningQueueHasTask(task: DTask): Boolean {
        val hasUrl = mRunningTasksUrl.contains(task.url())
        val hasTask = mRunningTasks.contains(task)
        return hasUrl && hasTask
    }


    @Synchronized
    fun putTaskToRunning(task: DTask, deleteTaskQueue: Boolean = false): Boolean {
        return if (mRunningTasks.size < DdNet.instance.ddNetConfig.maxDownloadNum) {
            mRunningTasks.add(task)
            mRunningTasksUrl.add(task.url()!!)
            if (deleteTaskQueue) {
                mTaskQueue.remove(task)
                mTaskQueueUrl.remove(task.url())
            }
            true
        } else {
            false
        }
    }

    @Synchronized
    fun putTaskToQueue(task: DTask) {
        mTaskQueue.add(task)
        mTaskQueueUrl.add(task.url())
    }


    @Synchronized
    fun isDownload(task: DTask): Boolean {
        if (task.url().equals(task.cancel())) {
            handler?.sendEmptyMessage(CANCEL_TASK)
            return false
        }
        if (!taskQueueHasTask(task) && !runningQueueHasTask(task)) {
            if (putTaskToRunning(task)) return true
        }

        if (taskQueueHasTask(task) && !runningQueueHasTask(task)) {
            if (putTaskToRunning(task, true)) return true
        }

        if (!taskQueueHasTask(task)) {
            putTaskToQueue(task)
        }

        return false
    }


    fun sendDownloadRequest(task: DTask, header: String?,callback: (type: Int, tag: String) -> Unit) {
        getBuilder(task, header).send(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                handleResult(task, DOWNLOAD_FILE, e.message.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                if (task.append()) {
                    if (response.code == 206) {
                        handleResponse(response, task,callback)
                    } else {
                        handleResult(task, DOWNLOAD_FILE, "不支持断点续传")
                    }
                } else {
                    if (response.code == 200) {
                        handleResponse(response, task,callback)
                    } else {
                        handleResult(task, DOWNLOAD_FILE, "response.code() = ${response.code}")
                    }
                }
            }
        })
    }

    private fun handleResult(task: DTask, type: Int, tag: String) {
        synchronized(this) {
            mRunningTasksUrl.remove(task.url())
            mRunningTasks.remove(task)
        }
        if (type == DOWNLOAD_FILE) {
            task.progressCallback()?.onFail(tag, task.url())
        } else if (type == DOWNLOAD_SUCCESS) {
            task.progressCallback()?.onProgress(task)
        }
        handler?.sendEmptyMessage(DOWNLOAD_TASK)
    }

    private fun getBuilder(task: DTask, header: String?): Builder {
        val builder = DdNet.instance.builder(DdNet.GET).url(task.url())
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
        val file = File(task.path(), ".tmp")
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
                callback.invoke(DOWNLOAD_FILE, "response.body is null")
            } else {
                saveNetStream(response.body!!.byteStream(), file, task, callback)
            }
            response.body?.close()
        } else {
            callback.invoke(DOWNLOAD_FILE, "创建文件夹失败")
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
                    File(task.path().toString() + ".tmp").delete()
                    callback.invoke(DOWNLOAD_FILE, "下载任务被主动取消")
                    return
                } else {
                    if (cur != pre) {
                        if (cur == 100) {
                            file.renameTo(File(file.absolutePath.replace(".tmp", "")))
                            callback.invoke(DOWNLOAD_SUCCESS, "任务下载成功")
                        } else {
                            task.progressCallback()?.onProgress(task)
                        }
                    }
                }
                pre = cur
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            callback.invoke(DOWNLOAD_FILE, "FileNotFoundException: not found file exception")
        } catch (e: IOException) {
            e.printStackTrace()
            callback.invoke(DOWNLOAD_FILE, "IOException: io exception")
        } finally {
            try {
                out?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun cancelTask(url:String?){
        synchronized(this){
            mRunningTasks.forEach {
                if (it.url().equals(url)) {
                    it.cancel(url)
                }
            }

            mTaskQueue.forEach {
                if (it.url().equals(url)) {
                    it.cancel(url)
                }
            }
        }
    }

    fun isQueue(url:String?):Boolean{
        synchronized(this) {
            return mRunningTasksUrl.contains(url) || mTaskQueueUrl.contains(url)
        }
    }
}