package com.itg.net.reqeust.body

import java.io.File

class IntervalBodyBuilder {
    private var file: File? = null
    private var offset: Long = 0

    fun addFile(file: File?): IntervalBodyBuilder {
        this.file = file
        return this
    }

    fun addFileOffset(offset: Long): IntervalBodyBuilder {
        this.offset = offset
        return this
    }

    fun build(): IntervalBody? {
        if (file == null) return null
        return IntervalBody(file!!, offset)
    }
}