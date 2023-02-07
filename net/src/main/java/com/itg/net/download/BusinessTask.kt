package com.itg.net.download

import android.content.ComponentName
import android.content.Intent
import android.os.Build
import com.itg.net.DdNet
import com.itg.net.Download
import com.itg.net.download.interfaces.IProgressCallback
import com.itg.net.download.interfaces.Task

class BusinessTask : DTask() {

    init {
        registerProgressListener()
    }

    private fun registerProgressListener(): BusinessTask {
        progressBack(object : IProgressCallback {
            override fun onConnecting(task: Task?) {
                if (task == null) return
                DdNet.instance.callbackMgr.loopConnecting(task as DTask)
            }

            override fun onProgress(task: Task?) {
                if (task == null) return
                DdNet.instance.callbackMgr.loop(task as DTask)
                if (task.getProgress() == 100 && (task.broadcast() || task.customBroadcast()
                        .orEmpty().isNotBlank())
                ) {
                    val intent = if (task.broadcast()) {
                        Intent(DdNet.BROAD_ACTION)
                    } else if (task.customBroadcast().orEmpty().isNotBlank()) {
                        Intent(task.customBroadcast())
                    } else {
                        null
                    }

                    if (Build.VERSION.SDK_INT >= 26 && task.broadcastComponentName().orEmpty().isNotBlank()) {
                        intent?.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP)//加上这句话，可以解决在android8.0系统以上2个module之间发送广播接收不到的问题
                        intent?.component = DdNet.instance.ddNetConfig.pkgName?.let { task.broadcastComponentName()?.let { it1 -> ComponentName(it, it1) } }
                    }
                    intent?.apply {
                        putExtra("url", task.url())
                        putExtra("file", task.path())
                        putExtra("extra", task.extra())
                        DdNet.instance.ddNetConfig.application?.sendBroadcast(intent)
                    }
                }
            }

            override fun onFail(error: String?, url: String?) {
                DdNet.instance.callbackMgr.loopFail(error ?: "", url ?: "")

            }
        })
        return this
    }

    fun prepareEnd(): ConversionPlugin {
        return ConversionPlugin(this)
    }

}