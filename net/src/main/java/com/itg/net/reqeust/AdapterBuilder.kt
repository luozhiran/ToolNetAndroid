//package com.itg.net.reqeust
//
//import android.app.Activity
//import android.net.Uri
//import android.os.Handler
//import android.os.Message
//import androidx.activity.ComponentActivity
//import com.itg.net.DdNet
//import com.itg.net.DdNet.Companion.MEDIA_JSON
//import com.itg.net.base.Builder
//import com.itg.net.base.DdCallback
//import com.itg.net.base.PostBuilder
//import com.itg.net.reqeust.body.IntervalBody
//import com.itg.net.reqeust.body.IntervalBodyBuilder
//import com.itg.net.reqeust.castration.IntervalFileCastrationBuilder
//import com.itg.net.reqeust.castration.JsonCastrationBuilder
//import okhttp3.*
//import okhttp3.MediaType.Companion.toMediaType
//import okhttp3.MediaType.Companion.toMediaTypeOrNull
//import okhttp3.RequestBody.Companion.asRequestBody
//import okhttp3.RequestBody.Companion.toRequestBody
//import org.json.JSONObject
//import java.io.File
//import java.io.IOException
//
//
//abstract class AdapterBuilder : PostBuilder {
//    private val call_is_null_msg = "url is error,please check url"
//    var url: String? = DdNet.instance.ddNetConfig.url
//    private val headerSb = StringBuilder()
//    protected val params = StringBuilder()
//
//    var files: MutableList<File?>? = null
//    var fileNames: MutableList<String?>? = null
//    var fileMediaTypes: MutableList<String?>? = null
//
//    var contents: MutableList<String?>? = null
//    var contentMediaTypes: MutableList<String?>? = null
//    var contentNames: MutableList<String?>? = null
//    private var activity: Activity? = null
//
//    var intervalOffset: Long = 0
//    var intervalFile: File? = null
//    var cookies: String? = null
//    var tag: String? = null
//    var json: String? = null
//
//    private val lifeObservable by lazy { MyLifecycleEventObserver() }
//
//
//    override fun addParam(key: String?, value: String?): Builder {
//        if (key.isNullOrBlank() || value.isNullOrBlank()) return this
//        params.append(key).append("#").append(value).append("$")
//        return this
//    }
//
//    override fun addParam(map: MutableMap<String, String?>?): Builder {
//        if (map.isNullOrEmpty()) return this
//        map.forEach { entry ->
//            addParam(entry.key, entry.value)
//        }
//        return this
//    }
//
//    override fun addHeader(key: String?, value: String?): Builder {
//        if (key.isNullOrBlank() || value.isNullOrBlank()) return this
//        headerSb.append(key).append("#").append(value).append("$")
//        return this
//    }
//
//    override fun addHeader(map: MutableMap<String, String?>?): Builder {
//        if (map.isNullOrEmpty()) return this
//        map.forEach { entry ->
//            addHeader(entry.key, entry.value)
//        }
//        return this
//    }
//
//    override fun url(url: String?): Builder {
//        this.url = url
//        return this
//    }
//
//    private fun initContentList() {
//        if (contentNames == null) {
//            contentNames = mutableListOf()
//        }
//        if (contents == null) {
//            contents = mutableListOf()
//        }
//        if (contentMediaTypes == null) {
//            contentMediaTypes = mutableListOf()
//        }
//    }
//
//    private fun initFileList() {
//        if (files == null) files = mutableListOf()
//        if (fileNames == null) fileNames = mutableListOf()
//        if (fileMediaTypes == null) fileMediaTypes = mutableListOf()
//    }
//
//    override fun addFile(file: File?): Builder = addFile("file", file)
//
//    override fun addFile(fileName: String?, file: File?): Builder = addFile(fileName, "", file)
//
//    override fun addFile(fileName: String?, mediaType: String?, file: File?): Builder {
//        if (file == null) return this
//        initFileList()
//        files?.add(file)
//        fileNames?.add(fileName)
//        fileMediaTypes?.add(mediaType)
//        return this
//    }
//
//
//    override fun addContent(content: String?, mediaType: String?): Builder =
//        addContent(content, "", mediaType)
//
//    override fun addContent(content: String?, contentFlag: String?, mediaType: String?): Builder {
//        if (content.isNullOrBlank()) return this
//        initContentList()
//        this.contents?.add(content)
//        this.contentNames?.add(contentFlag)
//        this.contentMediaTypes?.add(mediaType)
//        return this
//    }
//
//    override fun addJson(json: String?): JsonCastrationBuilder {
//        this.json = json
//        return JsonCastrationBuilder(this)
//    }
//
//    /**
//     * 使用改功能后，post不能携带其他数据，只能并且只能上传这一个文件
//     * @param file File?
//     * @param offset Long
//     * @return IntervalFileCastrationBuilder
//     */
//    override fun addInterval(file: File?, offset: Long): IntervalFileCastrationBuilder {
//        intervalFile = file
//        intervalOffset = offset
//        return IntervalFileCastrationBuilder(this)
//    }
//
//    override fun addCookie(cookie: Cookie?): Builder = addCookie(mutableListOf(cookie))
//
//    override fun addCookie(cookie: List<Cookie?>?): Builder {
//        if (cookie == null || cookie.isEmpty()) return this
//        val cookieHeader = StringBuilder()
//        cookie.forEachIndexed { index, value ->
//            if (index > 0) {
//                cookieHeader.append("; ")
//            }
//            if (value != null) {
//                cookieHeader.append(value.name).append("=").append(value.value)
//            }
//        }
//        cookies = cookieHeader.toString()
//        return this
//    }
//
//    override fun addTag(tag: String?): Builder {
//        this.tag = tag
//        return this
//    }
//
//    fun getHeader(): Headers? {
//        val builder: Headers.Builder? =
//            if (headerSb.isNotEmpty() || cookies.orEmpty().isNotEmpty()) {
//                Headers.Builder()
//            } else {
//                null
//            }
//
//        if (builder == null) return null
//        if (headerSb.isNotEmpty()) {
//            headerSb.toString()
//                .split("[$]")
//                .forEach { value ->
//                    val splitStr = value.split("#")
//                    if (splitStr.isNotEmpty() && splitStr.size == 2) {
//                        builder.add(splitStr[0], splitStr[1])
//                    }
//                }
//        }
//
//        if (cookies.orEmpty().isNotBlank()) {
//            builder.add("Cookie", cookies!!)
//        }
//        return builder.build()
//    }
//
//    /**
//     * 过滤参数，把可用参数和全局参数合并，并剔除重复参数
//     * @param requestParams StringBuilder?
//     */
//    protected fun mergeParam(requestParams: StringBuilder?): StringBuilder? {
//        val globalMap = DdNet.instance.ddNetConfig.globalParams
//        return if (globalMap.isNotEmpty()) {
//            val localBuild = StringBuilder()
//            val params = requestParams.toString()
//            globalMap.forEach {
//                val str = it.key + "#" + it.value
//                if (!params.contains(str)) {
//                    localBuild.append(str).append("$")
//                }
//            }
//            localBuild.append(requestParams)
//        } else {
//            requestParams
//        }
//    }
//
//    fun getParam(urlParams: StringBuilder? = null): String {
//        val urlParam = mergeParam(urlParams ?: params) ?: return this.url ?: ""
//        if (urlParam.isNotBlank()) {
//            val urlBuild = Uri.parse(this.url).buildUpon()
//            val keyValue = urlParam.toString().split("[$]")
//            if (keyValue.isEmpty()) return this.url ?: ""
//            keyValue.forEach { value ->
//                val s = value.split("#")
//                if (s.isNotEmpty() && s.size == 2) {
//                    urlBuild.appendQueryParameter(s[0], s[1])
//                }
//            }
//            this.url = urlBuild.build().toString()
//        }
//        return this.url ?: ""
//    }
//
//    abstract fun createCall(): Call?
//
//    override fun send(callback: DdCallback?) {
//        val call = createCall()
//        if (call == null) callback?.onFailure(call_is_null_msg)
//        registerEvent(call)
//        call?.enqueue(object : Callback {
//            override fun onFailure(call: Call, e: IOException) {
//                if (!call.isCanceled()) {
//                    callback?.onFailure(e.message)
//                }
//                finally()
//            }
//
//            override fun onResponse(call: Call, response: Response) {
//                if (!call.isCanceled()) {
//                    callback?.onResponse(response.body?.string(), response.code)
//                }
//                finally()
//            }
//        })
//    }
//
//    override fun send(handler: Handler?, what: Int, errorWhat: Int) {
//        val call = createCall()
//        if (call == null) {
//            val msg = Message.obtain()
//            msg.what = errorWhat
//            msg.obj = call_is_null_msg
//            handler?.sendMessage(msg)
//        }
//        registerEvent(call)
//        call?.enqueue(object : Callback {
//            override fun onFailure(call: Call, e: IOException) {
//                if (!call.isCanceled()) {
//                    val msg = Message.obtain()
//                    msg.what = errorWhat
//                    msg.obj = e.message
//                    handler?.sendMessage(msg)
//                }
//                finally()
//            }
//
//            override fun onResponse(call: Call, response: Response) {
//                if (!call.isCanceled()) {
//                    val msg = Message.obtain()
//                    msg.what = what
//                    msg.obj = response
//                    msg.obj = response.body?.string()
//                    handler?.sendMessage(msg)
//                }
//                finally()
//            }
//        })
//    }
//
//    override fun send(response: Callback?, callback: ((Call?) -> Unit)?) {
//        val call = createCall() ?: return
//        if (response != null) {
//            callback?.invoke(call)
//            call.enqueue(response)
//        }
//    }
//
//    override fun <T> asType(): T {
//        return this as T
//    }
//
//    override fun autoCancel(activity: Activity?): Builder {
//        this.activity = activity
//        return this
//    }
//
//
//    fun registerEvent(call: Call?) {
//        if (call == null) return
//        val tempActivity = (activity as? ComponentActivity)
//        lifeObservable.setCallback {
//            call.cancel()
//            unregisterEvent()
//        }
//        tempActivity?.lifecycle?.addObserver(lifeObservable)
//    }
//
//    fun unregisterEvent() {
//        val tempActivity = (activity as? ComponentActivity)
//        tempActivity?.runOnUiThread {
//            tempActivity.lifecycle.removeObserver(lifeObservable)
//            activity = null
//        }
//    }
//
//
//    fun finally() {
//        unregisterEvent()
//    }
//}