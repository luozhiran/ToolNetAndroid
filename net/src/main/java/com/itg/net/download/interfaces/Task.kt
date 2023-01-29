package com.itg.net.download.interfaces

interface Task {
    fun append(): Boolean

    fun getProgress(): Int

    fun getDownloadSize(): Long

    fun getContentLength(): Long

    fun cancel(cancel: String?)

    fun url(): String?

    fun broadcast(): Boolean

    fun broadcastComponentName(): String?

    fun path(): String?

    fun md5(): String?

    fun customBroadcast(): String?

    fun extra(): String?
}