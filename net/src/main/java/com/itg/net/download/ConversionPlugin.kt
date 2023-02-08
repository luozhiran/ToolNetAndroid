package com.itg.net.download

import android.app.Activity
import com.itg.net.DdNet
import com.itg.net.download.interfaces.IProgressCallback
import com.itg.net.download.interfaces.Task

class ConversionPlugin(val task: BusinessTask) {

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

    fun autoDestroyRequest(activity: Activity) {
        if (task.url().orEmpty().isBlank()) return
        if (DdNet.instance.needAutoCancelUrl.containsKey(activity)) {
            DdNet.instance.needAutoCancelUrl[activity]?.add(task.url()!!)
        } else {
            val list = mutableListOf<String>()
            list.add(task.url()!!)
            DdNet.instance.needAutoCancelUrl[activity] = list
        }
    }
}