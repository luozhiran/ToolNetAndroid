package com.itg.net.download

import com.itg.net.DdNet
import com.itg.net.download.interfaces.IProgressCallback

/**
 * 内部使用的下载回调
 * @property progressCallbackMap MutableMap<String, IProgressCallback>
 */
internal class TaskCallbackMgr {

    companion object{
        val instance: TaskCallbackMgr by lazy { TaskCallbackMgr() }
    }

    private val progressCallbackMap: MutableMap<String, IProgressCallback> by lazy { mutableMapOf() }


    fun loopConnecting(task:DTask){
        val uniqueId = getDownloadCallbackUniqueId(task)
        progressCallbackMap[uniqueId]?.onConnecting(task)
    }

    fun loop(task:DTask?){
        if (task == null)return
        val uniqueId = getDownloadCallbackUniqueId(task)
        progressCallbackMap[uniqueId]?.onProgress(task)

    }

    fun loopFail(msg:String,task: DTask?){
        if (task == null)return
        val uniqueId = getDownloadCallbackUniqueId(task)
        progressCallbackMap[uniqueId]?.onFail(msg,task)

    }

   private fun getDownloadCallbackUniqueId(task: DTask):String{
        return "${task.uniqueId}:${task.url()}"
    }

    fun setProgressCallback(task: DTask,progressCallback:IProgressCallback){
        val uniqueId = getDownloadCallbackUniqueId(task)
        progressCallbackMap[uniqueId] = progressCallback
    }

    fun removeProgressCallback(uniqueId:String){
        progressCallbackMap.remove(uniqueId)
    }

    fun removeProgressCallback(task: DTask){
        val uniqueId = getDownloadCallbackUniqueId(task)
        progressCallbackMap.remove(uniqueId)
    }

    fun getProgressCallback(task: DTask):IProgressCallback? {
        val uniqueId = getDownloadCallbackUniqueId(task)
        return progressCallbackMap[uniqueId]
    }
}