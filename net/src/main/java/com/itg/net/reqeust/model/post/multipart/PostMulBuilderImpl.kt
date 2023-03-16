package com.itg.net.reqeust.model.post.multipart

import android.app.Activity
import android.net.Uri
import android.os.Handler
import com.itg.net.DdNet
import com.itg.net.base.Builder
import com.itg.net.base.DdCallback
import com.itg.net.base.PostBuilder
import com.itg.net.reqeust.model.get.GetBuilder
import com.itg.net.reqeust.model.params.ParamsBuilder
import com.itg.net.reqeust.model.post.content.PostContentBuilder
import com.itg.net.reqeust.model.post.file.PostFileBuilder
import com.itg.net.reqeust.model.post.form.PostFormBuilder
import com.itg.net.reqeust.model.post.json.PostJsonBuilder
import okhttp3.*
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
abstract class PostMulBuilderImpl : ParamsBuilder(), PostBuilder, GetBuilder {

    private val urlParams = StringBuilder()
    private val postFile: PostFileBuilder by lazy {
        object : PostFileBuilder() {
            override fun autoCancel(activity: Activity?): Builder = this

            override fun send(callback: DdCallback?) {}

            override fun send(handler: Handler?, what: Int, errorWhat: Int) {}
            override fun send(response: Callback?, callback: ((Call?) -> Unit)?) {}
        }
    }
    private val postJson: PostJsonBuilder by lazy {
        object : PostJsonBuilder() {
            override fun autoCancel(activity: Activity?): Builder = this

            override fun send(callback: DdCallback?) {}

            override fun send(handler: Handler?, what: Int, errorWhat: Int) {}
            override fun send(response: Callback?, callback: ((Call?) -> Unit)?) {}
        }
    }
    private val postContent: PostContentBuilder by lazy {
        object : PostContentBuilder() {
            override fun autoCancel(activity: Activity?): Builder = this
            override fun send(callback: DdCallback?) {}
            override fun send(handler: Handler?, what: Int, errorWhat: Int) {}
            override fun send(response: Callback?, callback: ((Call?) -> Unit)?) {}
        }
    }
    private val postForm:PostFormBuilder by lazy { object : PostFormBuilder(){
        override fun autoCancel(activity: Activity?): PostFormBuilder = this
        override fun send(callback: DdCallback?) {}
        override fun send(handler: Handler?, what: Int, errorWhat: Int) {}
        override fun send(response: Callback?, callback: ((Call?) -> Unit)?) {}
    } }

    override fun addFile(file: File?): PostMulBuilderImpl {
        postFile.addFile1("file", file)
        return this
    }

    override fun addFile(fileName: String?, file: File?): PostMulBuilderImpl {
        postFile.addFile1(fileName, "", file)
        return this
    }

    override fun addFile(fileName: String?, mediaType: String?, file: File?): PostMulBuilderImpl {
        postFile.addFile1(fileName,mediaType,file)
       return this
    }

    override fun addContent(content: String?, mediaType: String?): PostMulBuilderImpl {
        postContent.addContent1(content, "", mediaType)
        return this;
    }

    override fun addContent(content: String?, contentFlag: String?, mediaType: String?): PostMulBuilderImpl {
        postContent.addContent1(content,contentFlag,mediaType)
        return this
    }

    override fun addParam(map: MutableMap<String, String?>?): PostMulBuilderImpl {
        postForm.addParam(map)
        return this
    }

    override fun addParam(key: String?, value: String?): PostMulBuilderImpl {
        postForm.addParam(key,value)
        return this
    }

    override fun addJson(json: String?): PostMulBuilderImpl {
        postJson.addJson1(json)
        return this
    }

    protected fun getRequestBody(): RequestBody? {
      return  getMultipartBody()
    }

    private fun getMultipartBody(): MultipartBody {
        val builder = MultipartBody.Builder()
        builder.setType(MultipartBody.FORM)
        var hasValue = false
        postForm.getRequestBody()?.let {
            hasValue = true
            builder.addPart(it)
        }
        for (index in 0 until postContent.getCount()) {
            val body = postContent.getRequestBody(index)
            if (body!=null) {
                hasValue = true
                builder.addFormDataPart(postContent.getContentName(index),null,body)
            }
        }

        for (index in 0 until postFile.getCount()) {
            postFile.getFileName(index)?.let {
                hasValue = true
                val body = postFile.getRequestBody(index)
                builder.addFormDataPart(it,postFile.getFileRealName(index),body)
            }
        }

        if (!hasValue) {
            builder.addFormDataPart("body", "not appropriate body")
        }
        return builder.build()
    }

    fun addAppendParams(key: String?, value: String?): PostMulBuilderImpl {
        urlParams.append(key).append("#").append(value).append("$")
        return this
    }

    internal fun getAppendParams():StringBuilder{
        return urlParams
    }

    /**
     * 把urlParams放到url的后面
     *
     */
    private fun mergeParam(params:StringBuilder): StringBuilder {
        val globalMap = DdNet.instance.ddNetConfig.globalParams
        return if (globalMap.isNotEmpty()) {
            val localBuild = StringBuilder()
            val params = params.toString()
            globalMap.forEach {
                val str = it.key + "#" + it.value
                if (!params.contains(str)) {
                    localBuild.append(str).append("$")
                }
            }
            localBuild.append(params)
        } else {
            params
        }
    }

    internal fun getUrl(): String {
        val urlParam = mergeParam(urlParams)
        if (urlParam.isNotBlank()) {
            val urlBuild = Uri.parse(this.url).buildUpon()
            val keyValue = urlParam.toString().split("$")
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