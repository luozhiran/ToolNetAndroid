package com.itg.net.reqeust.model.post.form

import android.app.Activity
import android.os.Handler
import com.itg.net.base.DdCallback
import com.itg.net.reqeust.model.base.SendTool
import okhttp3.Call
import okhttp3.Callback

class PostForm:PostFormBuilder() {
    private val sendTool by lazy { SendTool() }

    override fun autoCancel(activity: Activity?): PostForm {
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