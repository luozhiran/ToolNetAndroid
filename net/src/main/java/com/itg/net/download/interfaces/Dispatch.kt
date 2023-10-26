package com.itg.net.download.interfaces

import com.itg.net.download.Task

interface Dispatch {
    fun download(task: Task)
    fun appendDownload(task: Task)
}