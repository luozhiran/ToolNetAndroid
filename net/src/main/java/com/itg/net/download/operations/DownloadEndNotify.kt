package com.itg.net.download.operations

import android.content.ComponentName
import android.content.Intent
import android.os.Build
import com.itg.net.BROAD_ACTION
import com.itg.net.DdNet
import com.itg.net.download.Task
import com.itg.net.download.ERROR_TAG_11
import com.itg.net.download.ERROR_TAG_3
import com.itg.net.download.TaskCallbackMgr
import com.itg.net.download.interfaces.ITask

object DownloadEndNotify {

    @JvmStatic
    fun connectNotify(task: ITask?) {
        if (task == null) return
        DdNet.instance.callbackMgr.loopConnecting(task as Task)
        TaskCallbackMgr.instance.loopConnecting(task)
    }

    @JvmStatic
    fun progressNotify(task: ITask?) {
        if (task == null) return
        DdNet.instance.callbackMgr.loop(task as Task)
        TaskCallbackMgr.instance.loop(task)
    }

    @JvmStatic
    fun completeNotify(task: ITask?) {
        progressNotify(task)
        sendBroadcast(task)
    }

    @JvmStatic
    private fun sendBroadcast(task: ITask?) {
        if (task == null) return
        var intent: Intent? = null
        if (task.customBroadcast().orEmpty().isNotBlank()) {
            intent = Intent(task.customBroadcast())
        } else if (task.broadcast()) {
            intent = Intent(BROAD_ACTION)
        }
        intent?.let { it ->
            if (Build.VERSION.SDK_INT >= 26 && task.broadcastComponentName().orEmpty()
                    .isNotBlank()
            ) {
                it.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP)//加上这句话，可以解决在android8.0系统以上2个module之间发送广播接收不到的问题
                it.component = DdNet.instance.ddNetConfig.pkgName?.let {
                    task.broadcastComponentName()?.let { it1 -> ComponentName(it, it1) }
                }
            }
            it.putExtra("url", task.url())
            it.putExtra("file", task.path())
            it.putExtra("extra", task.extra())
            DdNet.instance.ddNetConfig.application?.sendBroadcast(it)
        }
    }


    @JvmStatic
    fun failNotify(task: ITask?, msg: String?) {
        if (task?.getProgress() ?: -1 > 0 && ERROR_TAG_3 != msg) {
            DdNet.instance.callbackMgr.loop(task as Task)
            TaskCallbackMgr.instance.loop(task)
        }
        if (ERROR_TAG_11 != msg) { // 重试下载，不抛给业务
            DdNet.instance.callbackMgr.loopFail(msg ?: "", task as? Task?)
            TaskCallbackMgr.instance.loopFail(msg ?: "", task as? Task?)
        }
    }

}