package com.itg.net.reqeust.model.post

import android.app.Activity
import com.itg.net.reqeust.castration.IntervalFileCastrationBuilder
import com.itg.net.reqeust.castration.JsonCastrationBuilder
import com.itg.net.reqeust.model.params.PostBuilderImpl
import okhttp3.Cookie
import okhttp3.MediaType
import java.io.File

/**
 * 提供外层方位使用链式调用，这里包裹一个参数生成器，返回值统一为改生成器
 * @property urlParams StringBuilder
 * @property formToJson Boolean
 * @property lifeObservable MyLifecycleEventObserver
 * @property activity Activity?
 */
abstract class PostGenerator : PostBuilderImpl() {

    protected val urlParams = StringBuilder()
    protected var formToJson = false
    protected var activity: Activity? = null

    override fun addParam(key: String?, value: String?): PostGenerator {
        super.addParam(key, value)
        return this
    }

    override fun addParam(map: MutableMap<String, String?>?): PostGenerator {
        super.addParam(map)
        return this
    }

    override fun addHeader(key: String?, value: String?): PostGenerator {
        super.addHeader(key, value)
        return this
    }

    override fun addHeader(map: MutableMap<String, String?>?): PostGenerator {
        super.addHeader(map)
        return this
    }

    override fun url(url: String?): PostGenerator {
        super.url(url)
        return this
    }

    override fun addCookie(cookie: Cookie?): PostGenerator {
        super.addCookie(cookie)
        return this
    }

    override fun addCookie(cookie: List<Cookie?>?): PostGenerator {
        super.addCookie(cookie)
        return this
    }

    override fun addTag(tag: String?): PostGenerator {
        super.addTag(tag)
        return this
    }


    override fun addFile(file: File?): PostGenerator {
        super.addFile(file)
        return this
    }

    override fun addFile(fileName: String?, file: File?): PostGenerator {
        super.addFile(fileName, file)
        return this
    }

    override fun addFile(fileName: String?, mediaType: String?, file: File?): PostGenerator {
        super.addFile(fileName, mediaType, file)
        return this
    }

    override fun addContent(content: String?, mediaType: String?): PostGenerator {
        super.addContent(content, mediaType)
        return this
    }

    override fun addContent(content: String?, contentFlag: String?, mediaType: String?): PostGenerator {
        super.addContent(content, contentFlag, mediaType)
        return this
    }

    override fun addJson(json: String?): JsonCastrationBuilder {
        return super.addJson(json)
    }

    override fun addInterval(file: File?, offset: Long): IntervalFileCastrationBuilder {
        return super.addInterval(file, offset)
    }

    override fun getFileType(fileName: String): MediaType? {
        return super.getFileType(fileName)
    }


    fun appendUrl(key: String?, value: String?): PostGenerator {
        urlParams.append(key).append("#").append(value).append("$")
        return this
    }
    /**
     * 表单转化成json
     * @return Post
     */
    fun formToJson(): PostGenerator {
        formToJson = true
        return this
    }

    override fun autoCancel(activity: Activity?): PostGenerator {
        this.activity = activity
        return this
    }


}