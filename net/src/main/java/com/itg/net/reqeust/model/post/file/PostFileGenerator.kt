package com.itg.net.reqeust.model.post.file

import com.itg.net.reqeust.model.params.ParamsBuilder
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

/**
 * 上传一个文件
 */
abstract class PostFileGenerator : PostFileBuilder() {

    fun addFile(file: File?): ParamsBuilder {
        return super.addFile1(file)
    }

    fun addFile(fileName: String?, file: File?): ParamsBuilder {
        return super.addFile1(fileName, file)
    }

    fun addFile(fileName: String?, mediaType: String?, file: File?): ParamsBuilder {
        return super.addFile1(fileName, mediaType, file)
    }



}