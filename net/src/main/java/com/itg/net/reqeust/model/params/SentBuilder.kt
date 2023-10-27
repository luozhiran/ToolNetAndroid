package com.itg.net.reqeust.model.params

import android.os.Handler
import com.itg.net.base.DdCallback
import com.itg.net.download.Task
import okhttp3.Call
import okhttp3.Callback

interface SentBuilder {
    fun send(callback: DdCallback?)
    fun send(handler: Handler?, what: Int, errorWhat: Int)
    fun send(response: Callback?,task:Task?){}
}