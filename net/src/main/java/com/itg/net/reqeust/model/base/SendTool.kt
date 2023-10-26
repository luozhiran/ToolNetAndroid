package com.itg.net.reqeust.model.base

import android.app.Activity
import android.os.Handler
import android.os.Message
import androidx.activity.ComponentActivity
import com.itg.net.DdNet
import com.itg.net.base.DdCallback
import com.itg.net.download.operations.PrincipalLife
import com.itg.net.reqeust.MyLifecycleEventObserver
import com.itg.net.reqeust.model.post.content.PostContent
import okhttp3.*
import java.io.IOException

class SendTool {
    private val principalLife by lazy { PrincipalLife() }

    fun autoCancel(activity: Activity?): SendTool {
        principalLife.addActivity(activity)
        return this
    }

    private fun registerEvent(call: Call?) {
        principalLife.registerEvent(call)
    }

    fun unregisterEvent() {
        principalLife.unregisterEvent()
    }

    fun combineParamsAndRCall(
        headers: Headers?,
        url: String?,
        tag: String?,
        body: RequestBody?,
        endCallback:((Request.Builder)->Unit)? = null
    ): Call? {
        val builder = Request.Builder()
        headers?.let {
            builder.headers(it)
        }

        if (tag.isNullOrEmpty()) {
            builder.tag(url)
        } else {
            builder.tag(tag)
        }
        if (url.orEmpty().isBlank()) {
            return null
        }
        url?.let {
            builder.url(it)
        }
        body?.let {
            builder.post(it)
        }
        endCallback?.invoke(builder)
        return DdNet.instance.okhttpManager.okHttpClient.newCall(builder.build())
    }


    fun send(callback: DdCallback?, call: Call?) {
        if (call == null) callback?.onFailure("url is error,please check url")
        registerEvent(call)
        call?.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                if (!call.isCanceled()) {
                    callback?.onFailure(e.message)
                }
                unregisterEvent()
            }

            override fun onResponse(call: Call, response: Response) {
                if (!call.isCanceled()) {
                    callback?.onResponse(response.body?.string(), response.code)
                }
                response.body?.close()
                unregisterEvent()
            }
        })
    }

    fun send(handler: Handler?, what: Int, errorWhat: Int, call: Call?) {
        if (call == null) {
            val msg = Message.obtain()
            msg.what = errorWhat
            msg.obj = "url is error,please check url"
            handler?.sendMessage(msg)
        }
        registerEvent(call)
        call?.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                if (!call.isCanceled()) {
                    val msg = Message.obtain()
                    msg.what = errorWhat
                    msg.obj = e.message
                    handler?.sendMessage(msg)
                }
                unregisterEvent()
            }

            override fun onResponse(call: Call, response: Response) {
                if (!call.isCanceled()) {
                    val msg = Message.obtain()
                    msg.what = what
                    msg.obj = response
                    msg.obj = response.body?.string()
                    handler?.sendMessage(msg)
                }
                response.body?.close()
                unregisterEvent()
            }
        })
    }

    fun send(response: Callback?, call: Call?, callback: ((Call?) -> Unit)?) {
        call ?: return
        if (response != null) {
            callback?.invoke(call)
            call.enqueue(response)
        }
    }


}