package com.itg.net.download

import android.util.Log
import com.itg.net.DdNet
import com.itg.net.download.interfaces.IProgressCallback
import com.itg.net.download.interfaces.Task

class ConversionPlugin(val task: BusinessTask) {

    fun setProgressListener(progressBack: IProgressCallback): ConversionPlugin {
        TaskCallbackMgr.instance.setProgressCallback(task,progressBack)
        return this
    }

    fun start(): Task {
        // 需要请求的url已经存在，所以需要删除注册的监听器，并且不发送下载请求
        if (DdNet.instance.download.dispatchTool.exit(task)) {
            Log.e("MainActivity", "任务已在队列")
            TaskCallbackMgr.instance.removeProgressCallback(task)
        } else {
            if (task.append()) {
                DdNet.instance.download.dispatchTool.appendDownload(task)
            } else {
                DdNet.instance.download.dispatchTool.download(task)
            }
        }
        return task
    }





}