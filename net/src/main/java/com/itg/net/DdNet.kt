package com.itg.net

import com.itg.net.base.Builder
import com.itg.net.download.CallbackMgr
import com.itg.net.reqeust.create
import com.itg.net.reqeust.model.Delete
import com.itg.net.reqeust.model.Get
import com.itg.net.reqeust.model.Post
import com.itg.net.reqeust.model.Put
import java.lang.Exception
import java.util.*


class DdNet {

    companion object {
        @JvmStatic
        val GET = 1

        @JvmStatic
        val POST = 2

        @JvmStatic
        val DELETE = 3

        @JvmStatic
        val PUT = 4

        @JvmStatic
        val MEDIA_JSON = "application/json; charset=utf-8"

        @JvmStatic
        val MEDIA_OCTET_STREAM = "application/octet-stream"

        @JvmStatic
        val BROAD_ACTION = "com.yqtec.install.broadcast"

        @JvmStatic
        val instance: DdNet by lazy { DdNet() }
    }

    val ddNetConfig: DdNetConfig by lazy { DdNetConfig() }
    val okhttpManager: OkhttpManager by lazy { OkhttpManager(ddNetConfig) }
    val download: Download by lazy { Download() }
    val callbackMgr: CallbackMgr by lazy { CallbackMgr() }

    fun builder(type: Int): Builder {
        return create(type) ?: throw Exception("dot support $type")
    }

    fun get() = builder(GET) as Get

    fun post() = builder(POST) as Post

    fun put() = builder(PUT) as Put

    fun delete() = builder(DELETE) as Delete



    fun cancelAll() {
        okhttpManager.okHttpClient.dispatcher.queuedCalls().forEach {
            it.cancel()
        }
        okhttpManager.okHttpClient.dispatcher.runningCalls().forEach {
            it.cancel()
        }
    }

    fun cancelTag(tag: Any?) {
        if (tag == null) return
        okhttpManager.okHttpClient.dispatcher.queuedCalls().forEach {
            if (tag == it.request().tag()) {
                it.cancel()
            }
        }
        okhttpManager.okHttpClient.dispatcher.runningCalls().forEach {
            if (tag == it.request().tag()) {
                it.cancel()
            }
        }
    }


    fun cancelFirstTag(tag: Any?) {
        if (tag == null) return
        okhttpManager.okHttpClient.dispatcher.queuedCalls().forEach {
            if (tag == it.request().tag()) {
                it.cancel()
                return
            }
        }
        okhttpManager.okHttpClient.dispatcher.runningCalls().forEach {
            if (tag == it.request().tag()) {
                it.cancel()
                return
            }
        }
    }

}