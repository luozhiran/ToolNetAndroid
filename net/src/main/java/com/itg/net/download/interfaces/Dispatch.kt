package com.itg.net.download.interfaces

import com.itg.net.download.DTask

interface Dispatch {
    fun download(task: DTask)
    fun appendDownload(task: DTask)
}