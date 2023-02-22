package com.itg.net.reqeust

import android.app.Activity
import android.net.Uri
import android.os.Handler
import android.os.Message
import androidx.activity.ComponentActivity
import com.itg.net.DdNet
import com.itg.net.DdNet.Companion.MEDIA_JSON
import com.itg.net.base.Builder
import com.itg.net.base.DdCallback
import com.itg.net.reqeust.body.IntervalBody
import com.itg.net.reqeust.body.IntervalBodyBuilder
import com.itg.net.reqeust.castration.IntervalFileCastrationBuilder
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.io.IOException


abstract class AdapterBuilder : Builder {

    private val call_is_null_msg = "url is error,please check url"
    var url: String? = DdNet.instance.ddNetConfig.url
    private val headerSb = StringBuilder()
    private val params = StringBuilder()

    var files: MutableList<File?>? = null
    var fileNames: MutableList<String?>? = null
    var fileMediaTypes: MutableList<String?>? = null

    var contents: MutableList<String?>? = null
    var contentMediaTypes: MutableList<String?>? = null
    var contentNames: MutableList<String?>? = null
    private var activity: Activity? = null

    var intervalOffset: Long = 0
    var intervalFile: File? = null
    var cookies: String? = null
    var tag: String? = null

    private val lifeObservable by lazy { MyLifecycleEventObserver()}


    override fun addParam(key: String?, value: String?): Builder {
        if (key.isNullOrBlank() || value.isNullOrBlank()) return this
        params.append(key).append("#").append(value).append("$")
        return this
    }

    override fun addParam(map: MutableMap<String, String?>?): Builder {
        if (map.isNullOrEmpty()) return this
        map.forEach { entry ->
            addParam(entry.key, entry.value)
        }
        return this
    }

    override fun addHeader(key: String?, value: String?): Builder {
        if (key.isNullOrBlank() || value.isNullOrBlank()) return this
        headerSb.append(key).append("#").append(value).append("$")
        return this
    }

    override fun addHeader(map: MutableMap<String, String?>?): Builder {
        if (map.isNullOrEmpty()) return this
        map.forEach { entry ->
            addHeader(entry.key, entry.value)
        }
        return this
    }

    override fun url(url: String?): Builder {
        this.url = url
        return this
    }

    private fun initContentList() {
        if (contentNames == null) {
            contentNames = mutableListOf()
        }
        if (contents == null) {
            contents = mutableListOf()
        }
        if (contentMediaTypes == null) {
            contentMediaTypes = mutableListOf()
        }
    }

    private fun initFileList() {
        if (files == null) files = mutableListOf()
        if (fileNames == null) fileNames = mutableListOf()
        if (fileMediaTypes == null) fileMediaTypes = mutableListOf()
    }

    override fun addFile(file: File?): Builder = addFile("file", file)

    override fun addFile(fileName: String?, file: File?): Builder = addFile(fileName, "", file)

    override fun addFile(fileName: String?, mediaType: String?, file: File?): Builder {
        if (file == null) return this
        initFileList()
        files?.add(file)
        fileNames?.add(fileName)
        fileMediaTypes?.add(mediaType)
        return this
    }


    override fun addContent(content: String?, mediaType: String?): Builder =
        addContent(content, "", mediaType)

    override fun addContent(content: String?, contentFlag: String?, mediaType: String?): Builder {
        if (content.isNullOrBlank()) return this
        initContentList()
        this.contents?.add(content)
        this.contentNames?.add(contentFlag)
        this.contentMediaTypes?.add(mediaType)
        return this
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
        return IntervalFileCastrationBuilder(this)
    }

    override fun addCookie(cookie: Cookie?): Builder = addCookie(mutableListOf(cookie))

    override fun addCookie(cookie: List<Cookie?>?): Builder {
        if (cookie == null || cookie.isEmpty()) return this
        val cookieHeader = StringBuilder()
        cookie.forEachIndexed { index, value ->
            if (index > 0) {
                cookieHeader.append("; ")
            }
            if (value != null) {
                cookieHeader.append(value.name).append("=").append(value.value)
            }
        }
        cookies = cookieHeader.toString()
        return this
    }

    override fun addTag(tag: String?): Builder {
        this.tag = tag
        return this
    }

    fun getHeader(): Headers? {
        val builder: Headers.Builder? =
            if (headerSb.isNotEmpty() || cookies.orEmpty().isNotEmpty()) {
                Headers.Builder()
            } else {
                null
            }

        if (builder == null) return null
        if (headerSb.isNotEmpty()) {
            headerSb.toString()
                .split("[$]")
                .forEach { value ->
                    val splitStr = value.split("#")
                    if (splitStr.isNotEmpty() && splitStr.size == 2) {
                        builder.add(splitStr[0], splitStr[1])
                    }
                }
        }

        if (cookies.orEmpty().isNotBlank()) {
            builder.add("Cookie", cookies!!)
        }
        return builder.build()
    }

    /**
     * 过滤参数，把可用参数和全局参数合并，并剔除重复参数
     * @param requestParams StringBuilder?
     */
    private fun mergeParam(requestParams: StringBuilder?): StringBuilder? {
        val globalMap = DdNet.instance.ddNetConfig.globalParams
        return if (globalMap.isNotEmpty()) {
            val localBuild = StringBuilder()
            val params = requestParams.toString()
            globalMap.forEach {
                val str = it.key + "#" + it.value
                if (!params.contains(str)) {
                    localBuild.append(str).append("$")
                }
            }
            localBuild.append(requestParams)
        } else {
            requestParams
        }
    }

    fun getParam(urlParams: StringBuilder? = null): String {
        val urlParam = mergeParam(urlParams ?: params) ?: return this.url ?: ""
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




    fun getRequestBody(formToJson:Boolean = false): RequestBody? {
        if (intervalFile != null) {
            val requestBody = getIntervalBody()
            if (requestBody != null) return requestBody
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
                getUpdateStringRequestBody(MEDIA_JSON,formToJson(formParams))
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

    private fun formToJson(formParams:StringBuilder):String{
        val formParamsArray = formParams.split("$")
        val json = JSONObject()
        formParams.forEach { str->
            val array = str.toString().split("#")
            if (array.size>1) {
                json.put(array[0],array[1])
            }
        }
        return json.toString()
    }

    private fun getFileType(fileName: String): MediaType? {
        return if (fileName.endsWith(".png")) {
            "image/png".toMediaTypeOrNull()
        } else if (fileName.endsWith(".jpg")) {
            "image/jpeg".toMediaTypeOrNull()
        } else {
            null
        }
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

    private fun getUpdateStringRequestBody(mediaType: String?, content: String?): RequestBody? {
        val mt = mediaType?.toMediaTypeOrNull()
        return content?.toRequestBody(mt)
    }

    private fun getUpdateFileRequestBody(file: File?): RequestBody? {
        val mt = "application/octet-stream".toMediaTypeOrNull()
        return file?.asRequestBody(mt)
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

    abstract fun createCall(): Call?

    override fun send(callback: DdCallback?) {
        val call = createCall()
        if (call == null) callback?.onFailure(call_is_null_msg)
        registerEvent(call)
        call?.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                if (!call.isCanceled()) {
                    callback?.onFailure(e.message)
                }
                finally()
            }

            override fun onResponse(call: Call, response: Response) {
                if (!call.isCanceled()) {
                    callback?.onResponse(response.body?.string(), response.code)
                }
                finally()
            }
        })
    }

    override fun send(handler: Handler?, what: Int, errorWhat: Int) {
        val call = createCall()
        if (call == null) {
            val msg = Message.obtain()
            msg.what = errorWhat
            msg.obj = call_is_null_msg
            handler?.sendMessage(msg)
        }
        registerEvent(call)
        call?.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                if (!call.isCanceled()) {
                    val msg = Message.obtain()
                    msg.what = errorWhat
                    msg.obj = e.message
                    handler?.sendMessage(msg)
                }
                finally()
            }

            override fun onResponse(call: Call, response: Response) {
                if (!call.isCanceled()) {
                    val msg = Message.obtain()
                    msg.what = what
                    msg.obj = response
                    msg.obj = response.body?.string()
                    handler?.sendMessage(msg)
                }
                finally()
            }
        })
    }

    override fun send(response: Callback?,callback:((Call?)->Unit)?) {
        val call = createCall() ?: return
        if (response != null) {
            callback?.invoke(call)
            call.enqueue(response)
        }
    }

    override fun <T> asType(): T {
        return this as T
    }

    override fun autoCancel(activity: Activity?): Builder {
        this.activity = activity
        return this
    }


    private fun registerEvent(call: Call?) {
        if (call == null) return
        val tempActivity = (activity as? ComponentActivity)
        lifeObservable.setCallback {
            call.cancel()
            unregisterEvent()
        }
        tempActivity?.lifecycle?.addObserver(lifeObservable)
    }

    private fun unregisterEvent() {
        val tempActivity = (activity as? ComponentActivity)
        tempActivity?.runOnUiThread {
            tempActivity.lifecycle.removeObserver(lifeObservable)
            activity = null
        }
    }


    private fun finally(){
        unregisterEvent()
    }
}