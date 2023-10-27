package com.itg.net.reqeust.model.post.json

import android.app.Activity
import android.os.Handler
import com.itg.net.base.DdCallback
import com.itg.net.reqeust.model.base.SendTool
import okhttp3.Call
import okhttp3.Callback

class PostJson:PostJsonGenerator() {

    private val sendTool by lazy { SendTool() }

    override fun autoCancel(activity: Activity?): PostJson {
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

}