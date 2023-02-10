package com.itg.ddlnet

import android.app.Application
import com.itg.net.DdNet

class App:Application() {

    override fun onCreate() {
        super.onCreate()
        
        DdNet.instance.ddNetConfig
            .app(this)
            .maxDownloadNum(1)
            .useHttpLog(true)
            .url("https://www.baidu.com")
    }
}