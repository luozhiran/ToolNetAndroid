package com.itg.net.reqeust.model.get

import android.app.Activity
import android.net.Uri
import com.itg.net.DdNet
import com.itg.net.reqeust.model.params.ParamsBuilder
import com.itg.net.reqeust.model.params.SentBuilder
import okhttp3.Cookie

abstract class GetGenerator: ParamsBuilder(), SentBuilder,GetBuilder {
    protected val params = StringBuilder()

    override fun addParam(key: String?, value: String?): GetGenerator {
        if (key.isNullOrBlank() || value.isNullOrBlank())  return this
        params.append(key).append("#").append(value).append("$")
        return this
    }

    override fun addParam(map: MutableMap<String, String?>?): GetGenerator {
        if (map.isNullOrEmpty()) return this
        map.forEach { entry ->
            addParam(entry.key, entry.value)
        }
        return this
    }

    override fun addHeader(key: String?, value: String?): GetGenerator {
        super.addHeader(key, value)
        return this
    }

    override fun addHeader(map: MutableMap<String, String?>?): GetGenerator {
        super.addHeader(map)
        return this
    }

    override fun url(url: String?): GetGenerator {
         super.url(url)
        return this
    }

    override fun addCookie(cookie: Cookie?): GetGenerator {
         super.addCookie(cookie)
        return this
    }

    override fun addCookie(cookie: List<Cookie?>?): GetGenerator {
        super.addCookie(cookie)
        return this
    }

    override fun addTag(tag: String?): GetGenerator {
        super.addTag(tag)
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
        val urlParam = mergeParam(params) ?: return this.url ?: ""
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