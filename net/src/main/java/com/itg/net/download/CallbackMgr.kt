package com.itg.net.download

import com.itg.net.download.interfaces.IProgressCallback

class CallbackMgr {
    private val lock: Any = Any()
    private val progressCallbackList : MutableList<IProgressCallback> by lazy { mutableListOf() }

    fun addProgressCallback(progressCallback:IProgressCallback){
        progressCallbackList.add(progressCallback)
    }

    fun removeProgressBack(progressCallback:IProgressCallback){
        progressCallbackList.remove(progressCallback)
    }



    fun loopConnecting(task:DTask){
        progressCallbackList.forEach {
            it.onConnecting(task)
        }
    }

    fun loop(task:DTask){
        progressCallbackList.forEach {
            it.onProgress(task)
        }
    }

    fun loopFail(msg:String,task: DTask?){
        if (task == null)return
        progressCallbackList.forEach {
            it.onFail(msg,task)
        }
    }

}