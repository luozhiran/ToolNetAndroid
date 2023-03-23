package com.itg.net.reqeust.model.post.json

import android.app.Activity
import okhttp3.Cookie

abstract class PostJsonGenerator:PostJsonBuilder() {

    fun addJson(json:String?): PostJsonBuilder {
        addJson1(json)
        return this
    }

    override fun autoCancel(activity: Activity?): PostJsonGenerator {
        return this
    }

    override fun addHeader(key: String?, value: String?): PostJsonGenerator {
        super.addHeader(key, value)
        return this
    }

    override fun addHeader(map: MutableMap<String, String?>?): PostJsonGenerator {
        super.addHeader(map)
        return this
    }

    override fun url(url: String?): PostJsonGenerator {
        super.url(url)
        return this
    }

    override fun addCookie(cookie: Cookie?): PostJsonGenerator {
        super.addCookie(cookie)
        return this
    }

    override fun addCookie(cookie: List<Cookie?>?): PostJsonGenerator {
        super.addCookie(cookie)
        return this
    }

    override fun addTag(tag: String?): PostJsonGenerator {
        super.addTag(tag)
        return this
    }
}