package com.itg.net.reqeust.model.post.json

import android.app.Activity
import android.net.Uri
import com.itg.net.DdNet
import com.itg.net.reqeust.model.params.ParamsBuilder
import com.itg.net.reqeust.model.post.form.PostFormBuilder
import com.itg.net.tools.StrTools
import com.itg.net.tools.UrlTools
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody


abstract class PostJsonBuilder : ParamsBuilder() {
   private var json: String? = null
    private val urlParams = StringBuilder()

   internal fun addJson1(json:String?): PostJsonBuilder {
        this.json = json
        return this
    }

    protected fun getRequestBody(): RequestBody {
        return json!!.toRequestBody("application/json;charset=utf-8".toMediaType());
    }

    fun addAppendParams(key: String?, value: String?): PostJsonBuilder {
        UrlTools.appendUrlParamsToStr(urlParams,key,value)
        return this
    }

    protected fun getAppendParams():StringBuilder{
        return urlParams
    }

    internal fun getUrl(): String {
        val urlParamsMap = UrlTools.cutOffStrToMap(urlParams.toString())
        val totalParamsMap = mutableMapOf<String,Any?>()
        totalParamsMap.putAll(DdNet.instance.ddNetConfig.globalParams)
        urlParamsMap?.let {
            totalParamsMap.putAll(it)
        }
        return UrlTools.getSpliceUrl(totalParamsMap,this.url?:"")
    }

    override fun autoCancel(activity: Activity?): PostJsonBuilder = this
}