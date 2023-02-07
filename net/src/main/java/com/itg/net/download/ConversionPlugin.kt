package com.itg.net.download

import com.itg.net.DdNet
import com.itg.net.download.interfaces.IProgressCallback
import com.itg.net.download.interfaces.Task

class ConversionPlugin(val task:BusinessTask) {

    fun setProgressListener(url: String, progressBack: IProgressCallback): ConversionPlugin {
        DdNet.instance.callbackMgr.addProgressCallbackToMap(url, progressBack)
        return this
    }

    fun start(): Task {
        if (task.append()) {
            DdNet.instance.download.dispatchTool.appendDownload(task)
        } else {
            DdNet.instance.download.dispatchTool.download(task)
        }
        return task
    }
}