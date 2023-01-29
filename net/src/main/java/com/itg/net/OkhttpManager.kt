package com.itg.net

import com.itg.net.interceptors.HttpLogger
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

class OkhttpManager(ddNetConfig: DdNetConfig) {
    var okHttpClient: OkHttpClient = if (ddNetConfig.getOkHttpClient() != null) {
        ddNetConfig.getOkHttpClient()!!
    } else {
        val builder = OkHttpClient.Builder()
        builder.connectTimeout(15, TimeUnit.SECONDS)
        builder.readTimeout(20, TimeUnit.SECONDS)
        builder.writeTimeout(35, TimeUnit.SECONDS)
        ddNetConfig.getInterceptors().let { list ->
            list.forEach { builder.addInterceptor(it) }
        }
        if (ddNetConfig.useHttpLog()) {
            val logInterceptor = HttpLoggingInterceptor(HttpLogger())
            logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            builder.addNetworkInterceptor(logInterceptor)
        }

        builder.build()
    }

}

