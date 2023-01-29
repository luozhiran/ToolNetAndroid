package com.itg.net

import com.itg.net.base.Builder
import com.itg.net.download.CallbackMgr
import com.itg.net.reqeust.create
import java.lang.Exception


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
        val instance :DdNet by lazy { DdNet() }
    }

    val ddNetConfig:DdNetConfig by lazy { DdNetConfig() }
    val okhttpManager:OkhttpManager by lazy { OkhttpManager(ddNetConfig) }
    val download:Download by lazy { Download() }
    val callbackMgr:CallbackMgr by lazy { CallbackMgr() }


    fun builder(type: Int): Builder {
        return create(type) ?: throw Exception("dot support $type")
    }

    fun cancelAll(){
        okhttpManager.okHttpClient.dispatcher.queuedCalls().forEach {
            it.cancel()
        }
        okhttpManager.okHttpClient.dispatcher.runningCalls().forEach {
            it.cancel()
        }
    }

    fun cancelTag(tag:Any?){
        if (tag == null)return
        okhttpManager.okHttpClient.dispatcher.queuedCalls().forEach {
            if (tag == it.request().tag()){
                it.cancel()
            }
        }
        okhttpManager.okHttpClient.dispatcher.runningCalls().forEach {
            if (tag == it.request().tag()){
                it.cancel()
            }
        }
    }

}