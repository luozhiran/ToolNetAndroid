package com.itg.net.base

import android.os.Handler
import com.itg.net.reqeust.model.Post
import okhttp3.Callback
import okhttp3.Cookie
import java.io.File

interface Builder {
    fun addParam(key: String?, value: String?): Builder
    fun addHeader(key: String?, value: String?): Builder
    fun url(url: String?): Builder
    fun send(callback: DdCallback?)
    fun send(handler: Handler?, what: Int, errorWhat: Int)
    fun send(response: Callback?)
    fun addFile(file: File?): Builder
    fun addFile(fileName: String?, file: File?): Builder
    fun addFile(fileName: String?, mediaType: String?, file: File?): Builder
    fun addContent(content: String?, mediaType: String?): Builder
    fun addContent(content: String?, contentFlag: String?, mediaType: String?): Builder
    fun addInterval(file: File?, offset: Long): Builder
    fun addCookie(cookie: Cookie?): Builder
    fun addCookie(cookie: List<Cookie?>?): Builder
    fun addTag(tag: String?): Builder
    fun <T> asType():T
}