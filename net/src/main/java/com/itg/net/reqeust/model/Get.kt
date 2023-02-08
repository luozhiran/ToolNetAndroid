package com.itg.net.reqeust.model

import android.os.Handler
import com.itg.net.DdNet
import com.itg.net.base.Builder
import com.itg.net.base.DdCallback
import com.itg.net.reqeust.AdapterBuilder
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request

class Get : AdapterBuilder() {
    override fun createCall(): Call? {
        val builder = Request.Builder()
        getHeader()?.apply { builder.headers(this) }
        val url = getParam()
        if (tag.isNullOrEmpty()) {
            builder.tag(url)
        } else {
            builder.tag(tag)
        }
        if (url.isBlank()) {
            return null
        }
        builder.url(url)
        builder.get()
        return DdNet.instance.okhttpManager.okHttpClient.newCall(builder.build())
    }


}