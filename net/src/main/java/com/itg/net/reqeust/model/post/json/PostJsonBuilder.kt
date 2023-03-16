package com.itg.net.reqeust.model.post.json

import android.net.Uri
import com.itg.net.DdNet
import com.itg.net.reqeust.model.params.ParamsBuilder
import com.itg.net.reqeust.model.post.form.PostFormBuilder
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody


abstract class PostJsonBuilder : ParamsBuilder() {
   private var json: String? = null
    private val urlParams = StringBuilder()

    fun addJson1(json:String?): PostJsonBuilder {
        this.json = json
        return this
    }

    protected fun getRequestBody(): RequestBody {
        return json!!.toRequestBody("application/json;charset=utf-8".toMediaType());
    }

    fun addAppendParams(key: String?, value: String?): PostJsonBuilder {
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