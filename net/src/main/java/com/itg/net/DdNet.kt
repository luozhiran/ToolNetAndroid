package com.itg.net

import com.itg.net.download.CallbackMgr
import com.itg.net.reqeust.create
import com.itg.net.reqeust.model.get.Get
import com.itg.net.reqeust.model.post.multipart.PostMul
import com.itg.net.reqeust.model.params.ParamsBuilder
import com.itg.net.reqeust.model.post.content.PostContent
import com.itg.net.reqeust.model.post.file.PostFile
import com.itg.net.reqeust.model.post.form.PostForm
import com.itg.net.reqeust.model.post.json.PostJson
import java.lang.Exception


const val MEDIA_JSON = "application/json; charset=utf-8"

const val MEDIA_OCTET_STREAM = "application/octet-stream"

const val BROAD_ACTION = "com.yqtec.install.broadcast"

enum class ModeType{PostFile,PostForm,PostJson,PostMul,Get,PostResume,PostContent}
class DdNet {

    companion object {

        @JvmStatic
        val instance: DdNet by lazy { DdNet() }
    }

    val ddNetConfig: DdNetConfig by lazy { DdNetConfig() }
    val okhttpManager: OkhttpManager by lazy { OkhttpManager(ddNetConfig) }
    val download: Download by lazy { Download() }
    val callbackMgr: CallbackMgr by lazy { CallbackMgr() }

    fun builder(type: ModeType): ParamsBuilder {
        return create(type) ?: throw Exception("dot support $type")
    }

    fun get() = builder(ModeType.Get) as Get

    fun postMultipart() = builder(ModeType.PostMul) as PostMul

    fun postFile() = builder(ModeType.PostFile) as PostFile

    fun postForm() = builder(ModeType.PostForm) as PostForm

    fun postJson() = builder(ModeType.PostJson) as PostJson

    fun postContent() = builder(ModeType.PostContent) as PostContent


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