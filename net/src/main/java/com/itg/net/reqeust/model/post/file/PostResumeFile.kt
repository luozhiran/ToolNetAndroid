package com.itg.net.reqeust.model.post.file

import android.app.Activity
import android.os.Handler
import android.os.Message
import androidx.activity.ComponentActivity
import com.itg.net.DdNet
import com.itg.net.base.DdCallback
import com.itg.net.reqeust.MyLifecycleEventObserver
import com.itg.net.reqeust.model.base.SendTool
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class PostResumeFile: PostResumeGenerator() {

    private val sendTool by lazy { SendTool() }

    override fun autoCancel(activity: Activity?): PostResumeFile {
        sendTool.autoCancel(activity)
        return this
    }

    override fun send(callback: DdCallback?) {
        val call = sendTool.combineParamsAndRCall(getHeader(),getUrl(),tag,getRequestBody())
        sendTool.send(callback, call)
    }

    override fun send(handler: Handler?, what: Int, errorWhat: Int) {
        val call = sendTool.combineParamsAndRCall(getHeader(),getUrl(),tag,getRequestBody())
        sendTool.send(handler,what,errorWhat,call)
    }

    override fun send(response: Callback?, callback: ((Call?) -> Unit)?) {
        val call = sendTool.combineParamsAndRCall(getHeader(),getUrl(),tag,getRequestBody())
        sendTool.send(response,call,callback)
    }

}