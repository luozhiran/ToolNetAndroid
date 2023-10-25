package com.itg.net.reqeust.model.post.content

import android.app.Activity
import android.net.Uri
import com.itg.net.DdNet
import com.itg.net.base.Builder
import com.itg.net.reqeust.model.params.ParamsBuilder
import com.itg.net.tools.UrlTools
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

abstract class PostContentBuilder : ParamsBuilder() {
    private var contents: MutableList<String?>? = null
    private var contentMediaTypes: MutableList<String?>? = null
    private var contentNames: MutableList<String?>? = null
    private val urlParams = StringBuilder()
    init {
        contents = mutableListOf()
        contentMediaTypes = mutableListOf()
        contentNames = mutableListOf()
    }
   internal fun addContent1(content: String?, mediaType: String?): PostContentBuilder =
        addContent1(content, "", mediaType)

   internal fun addContent1(
        content: String?,
        contentFlag: String?,
        mediaType: String?
    ): PostContentBuilder {
        this.contents?.add(content)
        this.contentNames?.add(contentFlag)
        this.contentMediaTypes?.add(mediaType)
        return this
    }

    internal fun getRequestBody(): RequestBody? {
        if (contentMediaTypes == null || contentMediaTypes?.size == 0) {
            return null
        }
        val mt = contentMediaTypes?.get(0)?.toMediaTypeOrNull()
        return contents?.get(0)?.toRequestBody(mt)
    }

    internal fun getCount():Int {
        return this.contents?.size?:0
    }
    internal fun getContentName(index:Int):String{
        return contentNames?.get(index)?:""
    }

    internal fun getRequestBody(index:Int): RequestBody?{
        val mt = contentMediaTypes?.get(index)?.toMediaTypeOrNull()
        return contents?.get(index)?.toRequestBody(mt)
    }

    internal fun addAppendParams(key: String?, value: String?): PostContentBuilder {
        UrlTools.appendUrlParamsToStr(urlParams,key,value)
        return this
    }

   protected fun getAppendParams():StringBuilder{
        return urlParams;
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

    override fun autoCancel(activity: Activity?): PostContentBuilder = this
}