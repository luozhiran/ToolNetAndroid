package com.itg.net.reqeust.model.post.file

import android.app.Activity
import com.itg.net.base.Builder
import com.itg.net.reqeust.model.params.ParamsBuilder
import okhttp3.Cookie
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

/**
 * 上传一个文件
 */
abstract class PostFileGenerator : PostFileBuilder() {

    fun addFile(file: File?): ParamsBuilder {
         super.addFile1(file)
        return this
    }

    fun addFile(fileName: String?, file: File?): ParamsBuilder {
        super.addFile1(fileName, file)
        return this
    }

    fun addFile(fileName: String?, mediaType: String?, file: File?): ParamsBuilder {
        super.addFile1(fileName, mediaType, file)
        return this
    }

    override fun autoCancel(activity: Activity?): ParamsBuilder {
        return this
    }

    override fun addHeader(key: String?, value: String?): ParamsBuilder {
         super.addHeader(key, value)
        return this
    }

    override fun addHeader(map: MutableMap<String, String?>?): ParamsBuilder {
         super.addHeader(map)
        return this
    }

    override fun url(url: String?): ParamsBuilder {
         super.url(url)
        return this
    }

    override fun addCookie(cookie: Cookie?): ParamsBuilder {
         super.addCookie(cookie)
        return this
    }

    override fun addCookie(cookie: List<Cookie?>?): ParamsBuilder {
         super.addCookie(cookie)
        return this
    }

    override fun addTag(tag: String?): ParamsBuilder {
         super.addTag(tag)
        return this
    }
}