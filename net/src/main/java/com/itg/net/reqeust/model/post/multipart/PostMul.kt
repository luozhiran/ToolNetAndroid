package com.itg.net.reqeust.model.post.multipart

import android.app.Activity
import android.os.Handler
import android.os.Message
import androidx.activity.ComponentActivity
import com.itg.net.DdNet
import com.itg.net.base.DdCallback
import com.itg.net.reqeust.MyLifecycleEventObserver
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class PostMul : PostMulGenerator() {
    private val lifeObservable by lazy { MyLifecycleEventObserver() }
    private var activity: Activity? = null


    override fun autoCancel(activity: Activity?): PostMulGenerator {
        this.activity = activity
        return this
    }


   private fun registerEvent(call: Call?) {
        if (call == null) return
        val tempActivity = (activity as? ComponentActivity)
        lifeObservable.setCallback {
            call.cancel()
            unregisterEvent()
        }
        tempActivity?.lifecycle?.addObserver(lifeObservable)
    }

   private fun unregisterEvent() {
        val tempActivity = (activity as? ComponentActivity)
        tempActivity?.runOnUiThread {
            tempActivity.lifecycle.removeObserver(lifeObservable)
            activity = null
        }
    }



    private fun createCall(): Call? {
        val builder = Request.Builder()
        getHeader()?.apply { builder.headers(this) }
        val url = getUrl()
        if (tag.isNullOrEmpty()) {
            builder.tag(url)
        } else {
            builder.tag(tag)
        }
        if (url.isBlank()) {
            return null
        }
        builder.url(url)
        getRequestBody()?.apply { builder.post(this) }
        return DdNet.instance.okhttpManager.okHttpClient.newCall(builder.build())
    }

   override fun send(callback: DdCallback?) {
        val call = createCall()
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
                unregisterEvent()
            }
        })
    }

    override fun send(handler: Handler?, what: Int, errorWhat: Int) {
        val call = createCall()
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
                unregisterEvent()
            }
        })
    }

    override  fun send(response: Callback?, callback:((Call?)->Unit)?) {
        val call = createCall() ?: return
        if (response != null) {
            callback?.invoke(call)
            call.enqueue(response)
        }
    }



}