package com.itg.net.download

import com.itg.net.download.interfaces.IProgressCallback

data class TempTask(val task: Task? = null, val iProgressCallback: IProgressCallback? = null)
