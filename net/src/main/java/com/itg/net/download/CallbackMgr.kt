package com.itg.net.download

import com.itg.net.download.interfaces.IProgressCallback

class CallbackMgr {
    private val lock: Any = Any()
    private val progressCallbackMap: MutableMap<String, IProgressCallback> by lazy { mutableMapOf() }
    private val progressCallbackList : MutableList<IProgressCallback> by lazy { mutableListOf() }

    fun addProgressCallbackToMap(url:String,progressCallback:IProgressCallback){
        progressCallbackMap[url] = progressCallback
    }

    fun removeProgressBackForMap(url:String){
        progressCallbackMap.remove(url)
    }

    fun switchProgressCallbackToMap(oldUrl:String,url:String):Boolean{
        if (oldUrl === url)return false
        if (progressCallbackMap.containsKey(oldUrl)) {
             progressCallbackMap.remove(oldUrl)?.apply {
                 progressCallbackMap[url] = this
                 return true
             }
        }
        return false
    }


    fun addProgressCallbackToList(progressCallback:IProgressCallback){
        progressCallbackList.add(progressCallback)
    }

    fun removeProgressBackForList(progressCallback:IProgressCallback){
        progressCallbackList.remove(progressCallback)
    }

    fun loopConnecting(task:DTask){
        progressCallbackMap[task.url()]?.onConnecting(task)
        progressCallbackList.forEach {
            it.onConnecting(task)
        }
    }

    fun loop(task:DTask){
        progressCallbackMap[task.url()]?.onProgress(task)
        progressCallbackList.forEach {
            it.onProgress(task)
        }
    }

    fun loopFail(msg:String,url:String){
        progressCallbackMap[url]?.onFail(msg,url)
        progressCallbackList.forEach {
            it.onFail(msg,url)
        }
    }
}