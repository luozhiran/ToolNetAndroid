package com.itg.net.reqeust.model.params

import com.itg.net.DdNet
import com.itg.net.base.Builder
import com.itg.net.base.PostBuilder
import com.itg.net.reqeust.body.IntervalBody
import com.itg.net.reqeust.body.IntervalBodyBuilder
import com.itg.net.reqeust.castration.IntervalFileCastrationBuilder
import com.itg.net.reqeust.castration.JsonCastrationBuilder
import okhttp3.FormBody
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File

/**
 * 对post的特有请求参数做处理，ParamsBuilder是共有参数
 * @property files MutableList<File?>?
 * @property fileNames MutableList<String?>?
 * @property fileMediaTypes MutableList<String?>?
 * @property contents MutableList<String?>?
 * @property contentMediaTypes MutableList<String?>?
 * @property contentNames MutableList<String?>?
 * @property intervalOffset Long
 * @property intervalFile File?
 * @property json String?
 */
abstract class PostBuilderImpl : ParamsBuilder(), PostBuilder {
    var files: MutableList<File?>? = null
    var fileNames: MutableList<String?>? = null
    var fileMediaTypes: MutableList<String?>? = null

    var contents: MutableList<String?>? = null
    var contentMediaTypes: MutableList<String?>? = null
    var contentNames: MutableList<String?>? = null


    var intervalOffset: Long = 0
    var intervalFile: File? = null

    var json: String? = null

    override fun addFile(file: File?): Builder = addFile("file", file)

    override fun addFile(fileName: String?, file: File?): Builder = addFile(fileName, "", file)

    override fun addFile(fileName: String?, mediaType: String?, file: File?): Builder {
        if (file == null) return this
        if (files == null) files = mutableListOf()
        if (fileNames == null) fileNames = mutableListOf()
        if (fileMediaTypes == null) fileMediaTypes = mutableListOf()
        files?.add(file)
        fileNames?.add(fileName)
        fileMediaTypes?.add(mediaType)
        return this
    }


    override fun addContent(content: String?, mediaType: String?): Builder =
        addContent(content, "", mediaType)

    override fun addContent(content: String?, contentFlag: String?, mediaType: String?): Builder {
        if (content.isNullOrBlank()) return this
        if (contentNames == null) contentNames = mutableListOf()
        if (contents == null) contents = mutableListOf()
        if (contentMediaTypes == null) contentMediaTypes = mutableListOf()
        this.contents?.add(content)
        this.contentNames?.add(contentFlag)
        this.contentMediaTypes?.add(mediaType)
        return this
    }

    override fun addJson(json: String?): JsonCastrationBuilder {
        this.json = json
        return JsonCastrationBuilder(this,this)
    }


    /**
     * 使用改功能后，post不能携带其他数据，只能并且只能上传这一个文件
     * @param file File?
     * @param offset Long
     * @return IntervalFileCastrationBuilder
     */
    override fun addInterval(file: File?, offset: Long): IntervalFileCastrationBuilder {
        intervalFile = file
        intervalOffset = offset
        return IntervalFileCastrationBuilder(this,this)
    }

    /***对参数的处理**/
    protected fun getRequestBody(formToJson: Boolean): RequestBody? {
        if (intervalFile != null) {
            val requestBody = getIntervalBody()
            if (requestBody != null) return requestBody
        }
        if (json?.isNotBlank() == true) {
            return json!!.toRequestBody("application/json;charset=utf-8".toMediaType());
        }

        val formParams = mergeParam(params)
        return if (contents.orEmpty()
                .isNotEmpty() && formParams.isNullOrBlank() && files.isNullOrEmpty()
        ) {
            getUpdateStringRequestBody(contentMediaTypes?.get(0), contents?.get(0))
        } else if (contents.isNullOrEmpty() && files.orEmpty()
                .isNotEmpty() && files?.size == 1 && formParams.isNullOrBlank()
        ) {
            getUpdateFileRequestBody(files?.get(0))
        } else if (!formParams.isNullOrEmpty() && contents.isNullOrEmpty() && files.isNullOrEmpty()) {
            if (formToJson) {
                getUpdateStringRequestBody(DdNet.MEDIA_JSON,formToJson(formParams))
            } else {
                getFromBody(formParams)
            }
        } else {
            getMultipartBody(formParams)
        }
    }


    private fun getIntervalBody(): IntervalBody? {
        val builder = IntervalBodyBuilder()
        builder.addFile(intervalFile)
        builder.addFileOffset(intervalOffset)
        return builder.build()
    }


    private fun getUpdateStringRequestBody(mediaType: String?, content: String?): RequestBody? {
        val mt = mediaType?.toMediaTypeOrNull()
        return content?.toRequestBody(mt)
    }

    private fun getUpdateFileRequestBody(file: File?): RequestBody? {
        val mt = "application/octet-stream".toMediaTypeOrNull()
        return file?.asRequestBody(mt)
    }

    private fun getFromBody(formParams: StringBuilder?): FormBody {
        val builder = FormBody.Builder()
        if (formParams == null) return builder.build()
        if (formParams.isNotEmpty()) {
            val keyValue = formParams.toString().split("[$]")
            if (keyValue.isEmpty()) return builder.build()
            keyValue.forEach {
                val s = it.split("#")
                if (s.isNotEmpty() && s.size == 2) {
                    builder.add(s[0], s[1])
                }
            }
            return builder.build()
        }
        return builder.build()
    }


    private fun getMultipartBody(formParams: StringBuilder?): MultipartBody {
        val builder = MultipartBody.Builder()
        builder.setType(MultipartBody.FORM)
        var hasValue = false
        if (formParams != null && formParams.isNotBlank()) {
            val keyValue = formParams.toString().split("[$]")
            keyValue.forEach {
                val s = it.split("#")
                if (s.isNotEmpty() && s.size == 2) {
                    builder.addFormDataPart(s[0], s[1])
                    hasValue = true
                }

            }
        }

        contents?.forEachIndexed { index, s ->
            val mediaType = contentMediaTypes?.get(index)?.toMediaTypeOrNull()
            val body = s?.toRequestBody(mediaType)
            contentNames?.get(index)?.let {
                if (body != null) {
                    builder.addFormDataPart(it, null, body)
                    hasValue = true
                }
            }
        }

        files?.forEachIndexed { index, file ->
            val mediaType = fileMediaTypes?.get(index) ?: getFileType(file?.name ?: "")
            val body = file?.asRequestBody(mediaType as MediaType?)
            fileNames?.get(index)?.let {
                if (file != null) {
                    if (body != null) {
                        builder.addFormDataPart(it, file.name, body)
                        hasValue = true
                    }
                }
            }
        }
        if (!hasValue) {
            builder.addFormDataPart("body", "not appropriate body")
        }
        return builder.build()
    }


    open fun getFileType(fileName: String): MediaType? {
        return if (fileName.endsWith(".png")) {
            "image/png".toMediaTypeOrNull()
        } else if (fileName.endsWith(".jpg")) {
            "image/jpeg".toMediaTypeOrNull()
        } else {
            null
        }
    }

    private fun formToJson(formParams:StringBuilder):String{
        val formParamsArray = formParams.split("$")
        val json = JSONObject()
        formParamsArray.forEach { str->
            val array = str.split("#")
            if (array.size>1) {
                json.put(array[0],array[1])
            }
        }
        return json.toString()
    }
}