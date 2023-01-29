package com.itg.net.download.interfaces

import com.itg.net.download.interfaces.Task

interface IProgressCallback {
    /**
     * 与服务器建立连接过程中
     *
     * @param task
     */
    fun onConnecting(task: Task?)

    fun onProgress(task: Task?)

    fun onFail(error: String?, url: String?)
}