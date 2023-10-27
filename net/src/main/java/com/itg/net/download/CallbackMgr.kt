package com.itg.net.download

import com.itg.net.download.interfaces.IProgressCallback
import com.itg.net.tools.TaskTools

class CallbackMgr {
    private val lock: Any = Any()
    private val progressCallbackList : MutableList<IProgressCallback> by lazy { mutableListOf() }

    fun addProgressCallback(progressCallback:IProgressCallback){
        synchronized(lock) {
            progressCallbackList.add(progressCallback)
        }
    }

    fun removeProgressBack(progressCallback:IProgressCallback){
        synchronized(lock) {
            progressCallbackList.remove(progressCallback)
        }
    }


   private fun getTempArray():MutableList<IProgressCallback> {
        val tempList = mutableListOf<IProgressCallback>()
       progressCallbackList.forEach {
           tempList.add(it)
       }
        return  tempList;
    }

    fun loopConnecting(task:Task){
        synchronized(lock) {
            getTempArray().forEach {
                it.onConnecting(task)
            }
        }

    }

    fun loop(task:Task){
        synchronized(lock) {
            getTempArray().forEach {
                it.onProgress(task,TaskTools.getDownloadProgress(task) == 100)
            }
        }
    }

    fun loopFail(msg:String,task: Task?){
        if (task == null)return
        synchronized(lock) {
            getTempArray().forEach {
                it.onFail(msg,task)
            }
        }
    }

}