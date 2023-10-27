package com.itg.net.download

import android.util.Log
import com.itg.net.download.interfaces.IProgressCallback
import com.itg.net.tools.TaskTools

/**
 * 内部使用的下载回调
 * @property progressCallbackMap MutableMap<String, IProgressCallback>
 */
internal class TaskCallbackMgr {

    companion object {
        val instance: TaskCallbackMgr by lazy { TaskCallbackMgr() }
    }

    private val progressCallbackMap: MutableMap<String, IProgressCallback> by lazy { mutableMapOf() }


    fun loopConnecting(task: Task) {
        val uniqueId = getDownloadCallbackUniqueId(task)
        progressCallbackMap[uniqueId]?.onConnecting(task)
    }

    fun loop(task: Task?) {
        if (task == null) return
        val uniqueId = getDownloadCallbackUniqueId(task)
        progressCallbackMap[uniqueId]?.onProgress(task, TaskTools.getDownloadProgress(task) == 100)

    }

    fun loopFail(msg: String, task: Task?) {
        if (task == null) return
        val uniqueId = getDownloadCallbackUniqueId(task)
        progressCallbackMap[uniqueId]?.onFail(msg, task)

    }

    private fun getDownloadCallbackUniqueId(task: Task): String {
        return task.url ?: ""
    }

    fun setProgressCallback(task: Task, progressCallback: IProgressCallback) {
        val uniqueId = getDownloadCallbackUniqueId(task)
        progressCallbackMap[uniqueId] = progressCallback
    }

    fun removeProgressCallback(uniqueId: String) {
        progressCallbackMap.remove(uniqueId)
    }

    fun removeProgressCallback(task: Task) {
        val uniqueId = getDownloadCallbackUniqueId(task)
        progressCallbackMap.remove(uniqueId)
    }

    fun getProgressCallback(task: Task): IProgressCallback? {
        val uniqueId = getDownloadCallbackUniqueId(task)
        return progressCallbackMap[uniqueId]
    }

    fun debugPrint(){
        Log.i(DEBUG_TAG,"全局监听器数量：${progressCallbackMap.size}")
    }
}