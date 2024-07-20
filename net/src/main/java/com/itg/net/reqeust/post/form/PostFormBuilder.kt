package com.itg.net.reqeust.post.form

import android.app.Activity
import com.itg.net.Net
import com.itg.net.reqeust.base.Builder
import com.itg.net.reqeust.get.GetBuilder
import com.itg.net.reqeust.base.ParamsBuilder
import com.itg.net.tools.UrlTools
import okhttp3.Cookie
import okhttp3.FormBody

abstract class PostFormBuilder : ParamsBuilder(), GetBuilder {
    private val urlParams = StringBuilder()
    private val params = StringBuilder()

    fun getRequestBody(): FormBody? {
        val formParams = UrlTools.cutOffStrToMap(params.toString())
        if (formParams.isNullOrEmpty()) return null
        val builder = FormBody.Builder()
        formParams.forEach {
            builder.add(it.key, it.value.toString())
        }
        return builder.build()
    }

    fun addAppendParams(key: String?, value: String?): PostFormBuilder {
        UrlTools.appendUrlParamsToStr(urlParams,key,value)
        return this
    }

   internal fun getAppendParams():StringBuilder{
        return urlParams;
    }

    override fun addParam(key: String?, value: String?): PostFormBuilder {
        UrlTools.appendUrlParamsToStr(params,key,value)
        return this
    }

    override fun addParam(map: MutableMap<String, String?>?): PostFormBuilder {
        if (map.isNullOrEmpty()) return this
        map.forEach { entry ->
            addParam(entry.key, entry.value)
        }
        return this
    }

    internal fun getParams():java.lang.StringBuilder{
        return params
    }

    internal fun getUrl(): String {
        val urlParamsMap = UrlTools.cutOffStrToMap(urlParams.toString())
        val totalParamsMap = mutableMapOf<String,Any?>()
        if (!this.noGlobalParams) {
            totalParamsMap.putAll(Net.instance.ddNetConfig.globalParams)
            urlParamsMap?.let {
                totalParamsMap.putAll(it)
            }
        }
        return UrlTools.getSpliceUrl(totalParamsMap,this.url?:"")
    }

    override fun addHeader(key: String?, value: String?): PostFormBuilder {
        super.addHeader(key, value)
        return this
    }

    override fun addHeader(map: MutableMap<String, String?>?): PostFormBuilder {
        super.addHeader(map)
        return this
    }

    override fun url(url: String?): PostFormBuilder {
        super.url(url)
        return this
    }

    override fun addCookie(cookie: Cookie?): PostFormBuilder {
         super.addCookie(cookie)
        return this
    }

    override fun addCookie(cookie: List<Cookie?>?): PostFormBuilder {
        super.addCookie(cookie)
        return this
    }

    override fun addTag(tag: String?): PostFormBuilder {
        super.addTag(tag)
        return this
    }

    override fun path(path: String): PostFormBuilder {
         super.path(path)
        return this
    }

    override fun autoCancel(activity: Activity?): PostFormBuilder {
        return this
    }

    override fun noUseGlobalParams(): PostFormBuilder {
        super.noUseGlobalParams()
        return this
    }
}