package com.itg.net.reqeust.model.post.file

import android.net.Uri
import com.itg.net.DdNet
import com.itg.net.reqeust.model.params.ParamsBuilder
import com.itg.net.reqeust.model.post.content.PostContentBuilder
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

abstract class PostFileBuilder : ParamsBuilder() {
    private var files: MutableList<File?>? = null
    private var fileNames: MutableList<String?>? = null
    private var fileMediaTypes: MutableList<String?>? = null

    //断点续传时使用
    private var intervalOffset: Long = 0
    private val urlParams = StringBuilder()

    init {
        files = mutableListOf()
        fileNames = mutableListOf()
        fileMediaTypes = mutableListOf()
    }

    internal fun addFile1(file: File?): PostFileBuilder = addFile1("file", file)
    internal fun addFile1(fileName: String?, file: File?): PostFileBuilder =
        addFile1(fileName, "", file)

    internal fun addFile1(fileName: String?, mediaType: String?, file: File?): PostFileBuilder {
        files?.add(file)
        fileNames?.add(fileName)
        fileMediaTypes?.add(mediaType)
        return this
    }

    open fun getRequestBody(): RequestBody? {
        return getRequestBody(0)
    }

    fun getRequestBody(index: Int): RequestBody {
        val file = files?.get(index) ?: File("")
        val mediaStr = if (fileMediaTypes?.size ?: 0 > index) {
            fileMediaTypes?.get(index)?.toMediaTypeOrNull()
        } else {
            getFileType(file.name)
        }
        return file.asRequestBody(mediaStr)
    }

    internal fun getFile(index: Int): File? {
        return files?.get(index)
    }

    internal fun getFileName(index: Int): String? {
        return fileNames?.get(index)
    }

    internal fun getFileRealName(index: Int): String? {
        return files?.get(index)?.name
    }

    internal fun getCount(): Int {
        return files?.size ?: 0
    }

    fun addResumeFileOffset1(intervalOffset: Long) {
        this.intervalOffset = intervalOffset;
    }

    protected fun getResumeFileOffset1(): Long {
        return this.intervalOffset ?: 0
    }

    private fun getFileType(fileName: String): MediaType? {
        return if (fileName.endsWith(".png")) {
            "image/png".toMediaTypeOrNull()
        } else if (fileName.endsWith(".jpg")) {
            "image/jpeg".toMediaTypeOrNull()
        } else {
            "application/octet-stream".toMediaTypeOrNull()
        }
    }

    fun addAppendParams(key: String?, value: String?): PostFileBuilder {
        urlParams.append(key).append("#").append(value).append("$")
        return this
    }

    protected fun getAppendParams(): StringBuilder {
        return urlParams
    }



    /**
     * 把urlParams放到url的后面
     *
     */
    internal fun mergeParam(sb:StringBuilder): StringBuilder {
        val globalMap = DdNet.instance.ddNetConfig.globalParams
        return if (globalMap.isNotEmpty()) {
            val localBuild = StringBuilder()
            val params = sb.toString()
            globalMap.forEach {
                val str = it.key + "#" + it.value
                if (!params.contains(str)) {
                    localBuild.append(str).append("$")
                }
            }
            localBuild.append(params)
        } else {
            sb
        }
    }

    internal fun getUrl(): String {
        val urlParam = mergeParam(urlParams) ?: return this.url ?: ""
        if (urlParam.isNotBlank()) {
            val urlBuild = Uri.parse(this.url).buildUpon()
            val keyValue = urlParam.toString().split("[$]")
            if (keyValue.isEmpty()) return this.url ?: ""
            keyValue.forEach { value ->
                val s = value.split("#")
                if (s.isNotEmpty() && s.size == 2) {
                    urlBuild.appendQueryParameter(s[0], s[1])
                }
            }
            this.url = urlBuild.build().toString()
        }
        return this.url ?: ""
    }

}