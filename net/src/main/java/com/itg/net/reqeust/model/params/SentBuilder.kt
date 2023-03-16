package com.itg.net.reqeust.model.params

import android.os.Handler
import com.itg.net.base.DdCallback
import okhttp3.Call
import okhttp3.Callback

interface SentBuilder {
    fun send(callback: DdCallback?)
    fun send(handler: Handler?, what: Int, errorWhat: Int)
    fun send(response: Callback?, callback:((Call?)->Unit)?)
}