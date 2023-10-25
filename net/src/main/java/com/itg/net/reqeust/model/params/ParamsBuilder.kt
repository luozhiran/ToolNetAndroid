package com.itg.net.reqeust.model.params

import android.app.Activity
import android.net.Uri
import com.itg.net.DdNet
import com.itg.net.base.Builder
import com.itg.net.reqeust.model.post.file.PostFileGenerator
import com.itg.net.tools.StrTools
import com.itg.net.tools.UrlTools
import okhttp3.Cookie
import okhttp3.Headers
import org.json.JSONObject

/**
 * 处理基础参数,post put delete get 所有共同需要的参数
 * @property url String?
 * @property headerSb StringBuilder
 * @property params StringBuilder
 * @property cookies String?
 * @property tag String?
 * @property json String?
 */
abstract class ParamsBuilder : Builder, SentBuilder {
    var url: String? = DdNet.instance.ddNetConfig.url
    private val headerStringBuilder = StringBuilder()
    var cookies: String? = null
    var tag: String? = null

    override fun addHeader(key: String?, value: String?): ParamsBuilder {
        if (key.isNullOrBlank() || value.isNullOrBlank()) return this
        UrlTools.appendUrlParamsToStr(headerStringBuilder,key,value)
        return this
    }

    override fun addHeader(map: MutableMap<String, String?>?): ParamsBuilder {
        if (map.isNullOrEmpty()) return this
        map.forEach { entry ->
            addHeader(entry.key, entry.value)
        }
        return this
    }

    override fun url(url: String?): ParamsBuilder {
        this.url = url
        return this
    }

    override fun addCookie(cookie: Cookie?): ParamsBuilder = addCookie(mutableListOf(cookie))

    override fun addCookie(cookie: List<Cookie?>?): ParamsBuilder {
        cookies = StrTools.getCookieString(cookie)
        return this
    }

    override fun addTag(tag: String?): ParamsBuilder {
        this.tag = tag
        return this
    }

    protected fun getHeader(): Headers? {
        if (headerStringBuilder.isBlank() && cookies.isNullOrBlank()) return null
        val builder: Headers.Builder = Headers.Builder()
        val headerParams = UrlTools.cutOffStrToMap(headerStringBuilder.toString())
        headerParams?.forEach{entry ->
            builder.add(entry.key, entry.value.toString())
        }
        if (cookies.orEmpty().isNotBlank()) {
            cookies?.let { builder.add("Cookie", it) }
        }
        return builder.build()
    }

    private fun formToJson(formParams: StringBuilder): String {
        val headerParams = UrlTools.cutOffStrToMap(formParams.toString())
        val json = JSONObject()
        headerParams?.forEach { entry ->
            json.put(entry.key, entry.value)
        }
        return json.toString()
    }

    override fun autoCancel(activity: Activity?): ParamsBuilder =this

}