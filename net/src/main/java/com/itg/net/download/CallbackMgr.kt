package com.itg.net.download

import com.itg.net.download.interfaces.IProgressCallback
import java.util.*

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

    fun loopConnecting(task:DTask){
        synchronized(lock) {
            getTempArray().forEach {
                it.onConnecting(task)
            }
        }

    }

    fun loop(task:DTask){
        synchronized(lock) {
            getTempArray().forEach {
                it.onProgress(task)
            }
        }
    }

    fun loopFail(msg:String,task: DTask?){
        if (task == null)return
        synchronized(lock) {
            getTempArray().forEach {
                it.onFail(msg,task)
            }
        }
    }

}