package com.itg.net.reqeust.model.post.content

import android.net.Uri
import com.itg.net.DdNet
import com.itg.net.reqeust.model.params.ParamsBuilder
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

internal abstract class PostContentBuilder : ParamsBuilder() {
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
        val mt = contentMediaTypes?.get(0)?.toMediaTypeOrNull()
        return contents?.get(0)?.toRequestBody(mt)
    }


    protected fun getCount():Int {
        return this.contents?.size?:0
    }

    protected fun getContentName(index:Int):String{
        return contentNames?.get(index)?:""
    }

    protected fun getRequestBody(index:Int): RequestBody?{
        val mt = contentMediaTypes?.get(index)?.toMediaTypeOrNull()
        return contents?.get(index)?.toRequestBody(mt)
    }

    internal fun addAppendParams(key: String?, value: String?): PostContentBuilder {
        urlParams.append(key).append("#").append(value).append("$")
        return this
    }

   protected fun getAppendParams():StringBuilder{
        return urlParams;
    }

    /**
     * 把urlParams放到url的后面
     *
     */
    private fun mergeParam(sb:StringBuilder): StringBuilder {
        val globalMap = DdNet.instance.ddNetConfig.globalParams
        return if (globalMap.isNotEmpty()) {
            val localBuild = StringBuilder()
            val params = sb.toString()
            globalMap.forEach {
                val str = it.key + "#" + it.value
                if (!params.contains(str)) {
                    localBuild.append(str).append("$")
                }
            }
            localBuild.append(params)
        } else {
            sb
        }
    }

    internal fun getUrl(): String {
        val urlParam = mergeParam(urlParams) ?: return this.url ?: ""
        if (urlParam.isNotBlank()) {
            val urlBuild = Uri.parse(this.url).buildUpon()
            val keyValue = urlParam.toString().split("$")
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