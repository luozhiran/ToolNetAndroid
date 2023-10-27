package com.itg.net.download

import android.util.Log
import com.itg.net.DdNet
import com.itg.net.download.interfaces.IProgressCallback
import com.itg.net.download.interfaces.ITask
import com.itg.net.download.operations.DownloadEndNotify
import com.itg.net.download.operations.PrincipalLife

class BusinessTask : Task() {
    init {
        registerProgressListener()
    }

    private fun registerProgressListener(): BusinessTask {
        progressBack(object : IProgressCallback {
            override fun onConnecting(task: ITask?) {
                DownloadEndNotify.connectNotify(task)
            }

            override fun onProgress(task: ITask?) {
                if (!DdNet.instance.download.dispatchTool.getTaskState().downloadComplete(task!!)) {
                    DownloadEndNotify.progressNotify(task)
                } else {
                    DownloadEndNotify.completeNotify(task)
                    testLog()
                }
            }

            override fun onFail(error: String?, task: ITask?) {
                DownloadEndNotify.failNotify(task, error)
                testLog()
            }
        })
        return this
    }

    fun prepareEnd(): ConversionPlugin {
        return ConversionPlugin(this)
    }

    fun testLog(){
        DdNet.instance.download.dispatchTool.getTaskState().debugPrint()
//        TaskCallbackMgr.instance.debugPrint()
        PrincipalLife.debugPrint()
    }

}