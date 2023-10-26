package com.itg.net.download.interfaces

interface IProgressCallback {
    /**
     * 与服务器建立连接过程中
     *
     * @param task
     */
    fun onConnecting(task: ITask?)

    fun onProgress(task: ITask?)

    fun onFail(error: String?, task: ITask?)
}