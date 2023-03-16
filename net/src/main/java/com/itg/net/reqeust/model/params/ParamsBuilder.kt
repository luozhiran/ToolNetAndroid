package com.itg.net.reqeust.model.params

import android.net.Uri
import com.itg.net.DdNet
import com.itg.net.base.Builder
import okhttp3.Cookie
import okhttp3.Headers

/**
 * 处理基础参数,post put delete get 所有共同需要的参数
 * @property call_is_null_msg String
 * @property url String?
 * @property headerSb StringBuilder
 * @property params StringBuilder
 * @property cookies String?
 * @property tag String?
 * @property json String?
 */
abstract class ParamsBuilder: Builder,SentBuilder{
    private val call_is_null_msg = "url is error,please check url"
    var url: String? = DdNet.instance.ddNetConfig.url
    private val headerSb = StringBuilder()
    protected val params = StringBuilder()
    var cookies: String? = null
    var tag: String? = null


    override fun addParam(key: String?, value: String?): ParamsBuilder {
        if (key.isNullOrBlank() || value.isNullOrBlank())  return this
        params.append(key).append("#").append(value).append("$")
        return this
    }

    override fun addParam(map: MutableMap<String, String?>?): ParamsBuilder {
        if (map.isNullOrEmpty()) return this
        map.forEach { entry ->
            addParam(entry.key, entry.value)
        }
        return this
    }

    override fun addHeader(key: String?, value: String?): ParamsBuilder {
        if (key.isNullOrBlank() || value.isNullOrBlank()) return this
        headerSb.append(key).append("#").append(value).append("$")
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
        if (cookie == null || cookie.isEmpty())  return this
        val cookieHeader = StringBuilder()
        cookie.forEachIndexed { index, value ->
            if (index > 0) {
                cookieHeader.append("; ")
            }
            if (value != null) {
                cookieHeader.append(value.name).append("=").append(value.value)
            }
        }
        cookies = cookieHeader.toString()
        return this
    }


    override fun addTag(tag: String?): ParamsBuilder {
        this.tag = tag
        return this
    }

    fun getHeader(): Headers? {
        val builder: Headers.Builder? =
            if (headerSb.isNotEmpty() || cookies.orEmpty().isNotEmpty()) {
                Headers.Builder()
            } else {
                null
            }

        if (builder == null) return null
        if (headerSb.isNotEmpty()) {
            headerSb.toString()
                .split("[$]")
                .forEach { value ->
                    val splitStr = value.split("#")
                    if (splitStr.isNotEmpty() && splitStr.size == 2) {
                        builder.add(splitStr[0], splitStr[1])
                    }
                }
        }

        if (cookies.orEmpty().isNotBlank()) {
            builder.add("Cookie", cookies!!)
        }
        return builder.build()
    }

    /**
     * 过滤参数，把可用参数和全局参数合并，并剔除重复参数
     * @param requestParams StringBuilder?
     */
    protected fun mergeParam(requestParams: StringBuilder?): StringBuilder? {
        val globalMap = DdNet.instance.ddNetConfig.globalParams
        return if (globalMap.isNotEmpty()) {
            val localBuild = StringBuilder()
            val params = requestParams.toString()
            globalMap.forEach {
                val str = it.key + "#" + it.value
                if (!params.contains(str)) {
                    localBuild.append(str).append("$")
                }
            }
            localBuild.append(requestParams)
        } else {
            requestParams
        }
    }

    fun getParam(urlParams: StringBuilder? = null): String {
        val urlParam = mergeParam(urlParams ?: params) ?: return this.url ?: ""
        if (urlParam.isNotBlank()) {
            val urlBuild = Uri.parse(this.url).buildUpon()
            val keyValue = urlParam.toString().split("[$]")
            if (keyValue.isEmpty()) return this.url ?: ""
            keyValue.forEach { value ->
                val s = value.split("#")
                if (s.isNotEmpty() && s.size == 2) {
                    urlBuild.appendQueryParameter(s[0], s[1])
                }
            }
            this.url = urlBuild.build().toString()
        }
        return this.url ?: ""
    }


}