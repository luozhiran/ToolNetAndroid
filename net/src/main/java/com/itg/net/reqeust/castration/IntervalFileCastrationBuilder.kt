package com.itg.net.reqeust.castration

import android.app.Activity
import android.os.Handler
import com.itg.net.base.Builder
import com.itg.net.base.DdCallback
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Cookie

/**
 * 使用IntervalFile上传文件时，只能实现问价上传功能，其他功能不生效，故需要阉割掉Builder不生效功能
 * @property builder Builder
 * @constructor
 */
class IntervalFileCastrationBuilder(private val builder: Builder){
    fun addHeader(key: String?, value: String?): IntervalFileCastrationBuilder {
        builder.addHeader(key,value)
        return this
    }
    fun addHeader(map:MutableMap<String,String?>?): IntervalFileCastrationBuilder {
        builder.addHeader(map)
        return this
    }
    fun url(url: String?): IntervalFileCastrationBuilder {
        builder.url(url)
        return this
    }
    fun send(callback: DdCallback?){
        builder.send(callback)

    }
    fun send(handler: Handler?, what: Int, errorWhat: Int){
        builder.send(handler,what,errorWhat)
    }
    fun send(response: Callback?,callback:((Call?)->Unit)?){
        builder.send(response,callback)
    }
    fun addCookie(cookie: Cookie?): IntervalFileCastrationBuilder {
        builder.addCookie(cookie)
        return this
    }
    fun addCookie(cookie: List<Cookie?>?): IntervalFileCastrationBuilder {
        builder.addCookie(cookie)
        return this
    }
    fun addTag(tag: String?): IntervalFileCastrationBuilder {
        builder.addTag(tag)
        return this
    }
    fun autoCancel(activity: Activity?): IntervalFileCastrationBuilder {
        builder.autoCancel(activity)
        return this
    }


}