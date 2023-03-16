package com.itg.net.reqeust.castration

import android.app.Activity
import android.os.Handler
import com.itg.net.base.Builder
import com.itg.net.base.DdCallback
import com.itg.net.reqeust.model.params.SentBuilder
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Cookie

/**
 * 使用IntervalFile上传文件时，只能实现问价上传功能，其他功能不生效，故需要阉割掉Builder不生效功能
 * @property builder Builder
 * @constructor
 */
class JsonCastrationBuilder(private val builder: Builder,private val sender:SentBuilder){
    fun addHeader(key: String?, value: String?): JsonCastrationBuilder {
        builder.addHeader(key,value)
        return this
    }
    fun addHeader(map:MutableMap<String,String?>?): JsonCastrationBuilder {
        builder.addHeader(map)
        return this
    }
    fun url(url: String?): JsonCastrationBuilder {
        builder.url(url)
        return this
    }
    fun send(callback: DdCallback?){
        sender.send(callback)

    }
    fun send(handler: Handler?, what: Int, errorWhat: Int){
        sender.send(handler,what,errorWhat)
    }
    fun send(response: Callback?,callback:((Call?)->Unit)?){
        sender.send(response,callback)
    }
    fun addCookie(cookie: Cookie?): JsonCastrationBuilder {
        builder.addCookie(cookie)
        return this
    }
    fun addCookie(cookie: List<Cookie?>?): JsonCastrationBuilder {
        builder.addCookie(cookie)
        return this
    }
    fun addTag(tag: String?): JsonCastrationBuilder {
        builder.addTag(tag)
        return this
    }
    fun autoCancel(activity: Activity?): JsonCastrationBuilder {
        builder.autoCancel(activity)
        return this
    }


}