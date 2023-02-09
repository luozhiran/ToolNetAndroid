package com.itg.net.download

import android.app.Activity
import androidx.activity.ComponentActivity
import com.itg.net.DdNet
import com.itg.net.download.interfaces.IProgressCallback
import com.itg.net.download.interfaces.Task
import com.itg.net.reqeust.MyLifecycleEventObserver
import okhttp3.Call

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





}