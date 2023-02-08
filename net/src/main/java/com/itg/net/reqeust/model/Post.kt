package com.itg.net.reqeust.model

import com.itg.net.DdNet
import com.itg.net.reqeust.AdapterBuilder
import okhttp3.Call
import okhttp3.Request

class Post : AdapterBuilder() {
    private val urlParams= StringBuilder()

    fun appendUrl(key: String?, value: String?): Post {
        urlParams.append(key).append("#").append(value).append("$")
        return this
    }

    override fun createCall(): Call? {
        val builder = Request.Builder()
        getHeader()?.apply { builder.headers(this) }
        val url = getParam(urlParams)
        if (tag.isNullOrEmpty()) {
            builder.tag(url)
        } else {
            builder.tag(tag)
        }
        if (url.isBlank()) {
          return null
        }
        builder.url(url)
        getRequestBody()?.apply { builder.post(this) }
        return DdNet.instance.okhttpManager.okHttpClient.newCall(builder.build())
    }

}