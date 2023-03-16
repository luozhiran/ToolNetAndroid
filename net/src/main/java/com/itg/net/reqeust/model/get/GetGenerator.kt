package com.itg.net.reqeust.model.get

import android.app.Activity
import com.itg.net.reqeust.model.params.ParamsBuilder
import com.itg.net.reqeust.model.params.SentBuilder
import okhttp3.Cookie

abstract class GetGenerator: ParamsBuilder(), SentBuilder {

    protected var activity: Activity? = null

    override fun autoCancel(activity: Activity?): GetGenerator {
        this.activity = activity
        return this;
    }


    override fun addParam(key: String?, value: String?): GetGenerator {
        super.addParam(key, value)
        return this
    }

    override fun addParam(map: MutableMap<String, String?>?): GetGenerator {
        super.addParam(map)
        return this
    }

    override fun addHeader(key: String?, value: String?): GetGenerator {
        super.addHeader(key, value)
        return this
    }

    override fun addHeader(map: MutableMap<String, String?>?): GetGenerator {
        super.addHeader(map)
        return this
    }

    override fun url(url: String?): GetGenerator {
         super.url(url)
        return this
    }

    override fun addCookie(cookie: Cookie?): GetGenerator {
         super.addCookie(cookie)
        return this
    }

    override fun addCookie(cookie: List<Cookie?>?): GetGenerator {
        super.addCookie(cookie)
        return this
    }

    override fun addTag(tag: String?): GetGenerator {
        super.addTag(tag)
        return this
    }



}