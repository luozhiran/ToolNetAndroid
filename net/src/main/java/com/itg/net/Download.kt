package com.itg.net

import android.content.ComponentName
import android.content.Intent
import android.os.Build
import com.itg.net.DdNet.Companion.BROAD_ACTION
import com.itg.net.download.DTask
import com.itg.net.download.DispatchTool
import com.itg.net.download.interfaces.IProgressCallback
import com.itg.net.download.interfaces.Task

class Download {

    private var task: DTask? = null
    private val dispatchTool: DispatchTool by lazy { DispatchTool() }

    private fun createTask(): DTask {
        if (task == null) {
            task = DTask()
        }
        return task!!
    }

    fun url(url: String): Download {
        createTask().url(url)
        return this
    }

    fun customBroadcast(action: String): Download {
        createTask().customBroadcast(action)
        return this
    }

    fun path(path: String): Download {
        createTask().path(path)
        return this
    }

    fun md5(md5: String): Download {
        createTask().md5(md5)
        return this
    }

    fun append(append: Boolean): Download {
        createTask().append(append)
        return this
    }

    fun extra(extra: String): Download {
        createTask().extra(extra)
        return this
    }

    fun setProgressListener(progressBack: IProgressCallback) {
        createTask().progressBack(progressBack)
    }

    fun registerProgressListener():Download {
        createTask().progressBack(object : IProgressCallback {
            override fun onConnecting(task: Task?) {
                DdNet.instance.callbackMgr.loopConnecting(task as DTask)
            }

            override fun onProgress(task: Task?) {
                DdNet.instance.callbackMgr.loop(task as DTask)
                if (task.getProgress() == 100 && (task.broadcast() || task.customBroadcast()
                        .orEmpty().isNotBlank())
                ) {
                    val intent = if (task.broadcast()) {
                        Intent(BROAD_ACTION)
                    } else if (task.customBroadcast().orEmpty().isNotBlank()) {
                        Intent(task.customBroadcast())
                    } else {
                        null
                    }

                    if (Build.VERSION.SDK_INT >= 26 && task.broadcastComponentName().orEmpty()
                            .isNotBlank()
                    ) {
                        intent?.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP)//加上这句话，可以解决在android8.0系统以上2个module之间发送广播接收不到的问题
                        intent?.component =
                            DdNet.instance.ddNetConfig.pkgName?.let {
                                task.broadcastComponentName()?.let { it1 -> ComponentName(it, it1) }
                            }
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


    fun broadcast(broad: Boolean): Download {
        createTask().broadcast(broad)
        return this
    }

    fun broadcastComponentName(componentName: String): Download {
        createTask().broadcastComponentName(componentName)
        return this
    }

    fun start() {
        task?.let {
            if (it.append()) {
                dispatchTool.appendDownload(it)
            } else {
                dispatchTool.download(it)
            }
            task = null
        }
    }

    fun isQueue(url: String): Boolean {
        return dispatchTool.isQueue(url)
    }

    fun cancel(url: String) {
        dispatchTool.cancelTask(url)
    }


}