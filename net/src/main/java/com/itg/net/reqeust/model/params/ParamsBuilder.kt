package com.itg.net.reqeust.model.params

import android.net.Uri
import com.itg.net.DdNet
import com.itg.net.base.Builder
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
    private val headerSb = StringBuilder()
    var cookies: String? = null
    var tag: String? = null

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
        if (cookie == null || cookie.isEmpty()) return this
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

    internal fun getHeader(): Headers? {
        val builder: Headers.Builder? =
            if (headerSb.isNotEmpty() || cookies.orEmpty().isNotEmpty()) {
                Headers.Builder()
            } else {
                null
            }

        if (builder == null) return null
        if (headerSb.isNotEmpty()) {
            headerSb.toString()
                .split("$")
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

    private fun formToJson(formParams: StringBuilder): String {
        val formParamsArray = formParams.split("$")
        val json = JSONObject()
        formParamsArray.forEach { str ->
            val array = str.split("#")
            if (array.size > 1) {
                json.put(array[0], array[1])
            }
        }
        return json.toString()
    }


}