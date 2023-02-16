package com.itg.net.base

import android.app.Activity
import android.os.Handler
import com.itg.net.reqeust.castration.IntervalFileCastrationBuilder
import com.itg.net.reqeust.model.Post
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Cookie
import java.io.File

interface Builder {
    fun addParam(map:MutableMap<String,String?>?): Builder
    fun addParam(key: String?, value: String?): Builder
    fun addHeader(key: String?, value: String?): Builder
    fun addHeader(map:MutableMap<String,String?>?): Builder
    fun url(url: String?): Builder
    fun send(callback: DdCallback?)
    fun send(handler: Handler?, what: Int, errorWhat: Int)
    fun send(response: Callback?,callback:((Call?)->Unit)?)
    fun addFile(file: File?): Builder
    fun addFile(fileName: String?, file: File?): Builder
    fun addFile(fileName: String?, mediaType: String?, file: File?): Builder
    fun addContent(content: String?, mediaType: String?): Builder
    fun addContent(content: String?, contentFlag: String?, mediaType: String?): Builder
    fun addInterval(file: File?, offset: Long): IntervalFileCastrationBuilder
    fun addCookie(cookie: Cookie?): Builder
    fun addCookie(cookie: List<Cookie?>?): Builder
    fun addTag(tag: String?): Builder
    fun autoCancel(activity: Activity?):Builder
    fun <T> asType():T
}