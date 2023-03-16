package com.itg.net.reqeust.model.post.form

import android.net.Uri
import com.itg.net.DdNet
import com.itg.net.reqeust.model.get.GetBuilder
import com.itg.net.reqeust.model.params.ParamsBuilder
import okhttp3.FormBody

abstract class PostFormBuilder : ParamsBuilder(),GetBuilder {
    private val urlParams = StringBuilder()
    protected val params = StringBuilder()

    fun getRequestBody(): FormBody? {
        val formParams = mergeParam(params)
        if (formParams.isBlank()) return null
        val builder = FormBody.Builder()
        if (formParams.isNotEmpty()) {
            val keyValue = formParams.toString().split("[$]")
            if (keyValue.isEmpty()) return null
            keyValue.forEach {
                val s = it.split("#")
                if (s.isNotEmpty() && s.size == 2) {
                    builder.add(s[0], s[1])
                }
            }
            return builder.build()
        }
        return builder.build()
    }

    fun addAppendParams(key: String?, value: String?): PostFormBuilder {
        urlParams.append(key).append("#").append(value).append("$")
        return this
    }

   internal fun getAppendParams():StringBuilder{
        return urlParams;
    }

    override fun addParam(key: String?, value: String?): PostFormBuilder {
        if (key.isNullOrBlank() || value.isNullOrBlank())  return this
        params.append(key).append("#").append(value).append("$")
        return this
    }

    override fun addParam(map: MutableMap<String, String?>?): PostFormBuilder {
        if (map.isNullOrEmpty()) return this
        map.forEach { entry ->
            addParam(entry.key, entry.value)
        }
        return this
    }



    /**
     * 把urlParams放到url的后面
     *
     */
    internal fun mergeParam(sb:StringBuilder): StringBuilder {
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