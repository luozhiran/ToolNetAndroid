package com.itg.net.download

import com.itg.net.download.interfaces.IProgressCallback
import com.itg.net.download.interfaces.Task

abstract class DTask : Task {

    var param: HashMap<String, String>? = null
    private var tUrl: String? = null
    var tMd5: String? = null
    private var tPath: String? = null
    private var iProgressCallback: IProgressCallback? = null
    private var rContentLength: Long = 0
    private var rDownloadSize: Long = 0
    private var rCancelUrl: String? = null
    private var tAppend = false
    private var tBroad = false
    private var tComponentName: String? = null
    private var tCostomBroadcast: String? = null
    private var tExtra: String? = null
    val uniqueId = System.currentTimeMillis()

    /**
     * 当前下载任务，如果支持断点续传，append设置为true
     * @param append Boolean
     */
    fun append(append: Boolean):BusinessTask {
        tAppend = append
        return this as BusinessTask
    }


    override fun append() = tAppend

    override fun getProgress(): Int {
        return if (rContentLength == 0L) {
            -1
        } else {
            (100 * rDownloadSize.toDouble() / rContentLength.toDouble()).toInt()
        }
    }

    override fun getDownloadSize() = rDownloadSize

    fun setDownloadSize(size:Long){
        rDownloadSize = size
    }

    override fun getContentLength() = rContentLength
    fun setContentLength(contentLength: Long) {
        rContentLength = contentLength
    }

    override fun cancel(cancel: String?) {
        rCancelUrl = cancel
    }

    fun cancel() = rCancelUrl


    fun url(url: String?): BusinessTask {
        tUrl = url
        return this as BusinessTask
    }


    override fun url() = tUrl

    override fun broadcast() = tBroad

    fun broadcast(broad: Boolean) {
        tBroad = broad
    }

    fun broadcastComponentName(componentName: String) {
        tComponentName = componentName
    }

    override fun broadcastComponentName() = tComponentName

    fun path(path: String): BusinessTask {
        tPath = path
        return this as BusinessTask
    }

    override fun path() = tPath


    override fun md5() = tMd5

    fun md5(md5: String?): BusinessTask {
        tMd5 = md5
        return this as BusinessTask
    }

    fun customBroadcast(action: String) {
        tCostomBroadcast = action
    }

    override fun customBroadcast() = tCostomBroadcast

    fun extra(extra: String) {
        tExtra = extra
    }

    override fun extra() = tExtra

    fun progressCallback() = iProgressCallback


    fun progressBack(callback: IProgressCallback?): BusinessTask? {
        iProgressCallback = callback
        return this as? BusinessTask
    }
}