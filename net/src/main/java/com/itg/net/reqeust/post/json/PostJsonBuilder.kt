package com.itg.net.reqeust.post.json

import android.app.Activity
import android.icu.number.IntegerWidth
import android.text.TextUtils
import com.itg.net.Net
import com.itg.net.reqeust.base.ParamsBuilder
import com.itg.net.reqeust.get.GetBuilder
import com.itg.net.reqeust.post.form.PostFormBuilder
import com.itg.net.tools.UrlTools
import okhttp3.Cookie
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject


abstract class PostJsonBuilder : ParamsBuilder(), GetBuilder {

    private val urlParams = StringBuilder()
    private val params = StringBuilder()
    private var jsonObject = JSONObject()
    private var noUseCommonParams = true

    internal fun addJson1(json: String?): PostJsonBuilder {
        val formParams = UrlTools.cutOffStrToMap(params.toString())
        if (formParams.isNullOrEmpty()) return this
        formParams.forEach { entry ->
            if (!TextUtils.isEmpty(entry.key)) {
                this.jsonObject.put(entry.key, entry.value)
            }
        }
        return this
    }

    protected fun getRequestBody(): RequestBody {
        return jsonObject.toString().toRequestBody("application/json;charset=utf-8".toMediaType());
    }

    fun addAppendParams(key: String?, value: String?): PostJsonBuilder {
        UrlTools.appendUrlParamsToStr(urlParams, key, value)
        return this
    }

    internal fun getAppendParams(): StringBuilder {
        return urlParams
    }

    override fun addParam(key: String?, value: String?): PostJsonBuilder {
        if (!TextUtils.isEmpty(key)) {
            if (key != null) {
                this.jsonObject.put(key, value)
            }
        }
        return this
    }

    fun addParam(key: String?, value: Long?): PostJsonBuilder {
        if (!TextUtils.isEmpty(key)) {
            if (key != null) {
                this.jsonObject.put(key, value)
            }
        }
        return this
    }

    fun addParam(key: String?, value: Int?): PostJsonBuilder {
        if (!TextUtils.isEmpty(key)) {
            if (key != null) {
                this.jsonObject.put(key, value)
            }
        }
        return this
    }

    fun addParam(key: String?, value: Float?): PostJsonBuilder {
        if (!TextUtils.isEmpty(key)) {
            if (key != null) {
                this.jsonObject.put(key, value)
            }
        }
        return this
    }

    fun noUseUrlCommonParams():PostJsonBuilder {
        this.noUseCommonParams = false
        return this;

    }

    override fun addParam(map: MutableMap<String, String?>?): PostJsonBuilder {
        if (map.isNullOrEmpty()) return this
        map.forEach { entry ->
            addParam(entry.key, entry.value)
        }
        return this
    }

    internal fun getParams(): java.lang.StringBuilder {
        return params
    }

    internal fun getUrl(): String {
        val urlParamsMap = UrlTools.cutOffStrToMap(urlParams.toString())
        val totalParamsMap = mutableMapOf<String, Any?>()
        if (this.noUseCommonParams) {
            totalParamsMap.putAll(Net.instance.ddNetConfig.globalParams)
            urlParamsMap?.let {
                totalParamsMap.putAll(it)
            }
        }
        return UrlTools.getSpliceUrl(totalParamsMap, this.url ?: "")
    }

    override fun addHeader(key: String?, value: String?): PostJsonBuilder {
        super.addHeader(key, value)
        return this
    }

    override fun addHeader(map: MutableMap<String, String?>?): PostJsonBuilder {
        super.addHeader(map)
        return this
    }

    override fun url(url: String?): PostJsonBuilder {
        super.url(url)
        return this
    }

    override fun addCookie(cookie: Cookie?): PostJsonBuilder {
        super.addCookie(cookie)
        return this
    }

    override fun addCookie(cookie: List<Cookie?>?): PostJsonBuilder {
        super.addCookie(cookie)
        return this
    }

    override fun addTag(tag: String?): PostJsonBuilder {
        super.addTag(tag)
        return this
    }

    override fun path(path: String): PostJsonBuilder {
        super.path(path)
        return this
    }

    override fun autoCancel(activity: Activity?): PostJsonBuilder {
        return this
    }
}