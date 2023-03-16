package com.itg.net.reqeust.model.post.multipart

import android.app.Activity
import com.itg.net.reqeust.model.get.GetBuilder
import okhttp3.Cookie
import java.io.File

/**
 * 提供外层方位使用链式调用，这里包裹一个参数生成器，返回值统一为改生成器
 * @property urlParams StringBuilder
 * @property formToJson Boolean
 * @property activity Activity?
 */
abstract class PostMulGenerator : PostMulBuilderImpl() {

    override fun addParam(key: String?, value: String?): PostMulGenerator {
        super.addParam(key, value)
        return this
    }

    override fun addParam(map: MutableMap<String, String?>?): PostMulGenerator {
        super.addParam(map)
        return this
    }

    override fun addHeader(key: String?, value: String?): PostMulGenerator {
        super.addHeader(key, value)
        return this
    }

    override fun addHeader(map: MutableMap<String, String?>?): PostMulGenerator {
        super.addHeader(map)
        return this
    }

    override fun url(url: String?): PostMulGenerator {
        super.url(url)
        return this
    }

    override fun addCookie(cookie: Cookie?): PostMulGenerator {
        super.addCookie(cookie)
        return this
    }

    override fun addCookie(cookie: List<Cookie?>?): PostMulGenerator {
        super.addCookie(cookie)
        return this
    }

    override fun addTag(tag: String?): PostMulGenerator {
        super.addTag(tag)
        return this
    }


    override fun addFile(file: File?): PostMulGenerator {
        super.addFile(file)
        return this
    }

    override fun addFile(fileName: String?, file: File?): PostMulGenerator {
        super.addFile(fileName, file)
        return this
    }

    override fun addFile(fileName: String?, mediaType: String?, file: File?): PostMulGenerator {
        super.addFile(fileName, mediaType, file)
        return this
    }

    override fun addContent(content: String?, mediaType: String?): PostMulGenerator {
        super.addContent(content, mediaType)
        return this
    }

    override fun addContent(content: String?, contentFlag: String?, mediaType: String?): PostMulGenerator {
        super.addContent(content, contentFlag, mediaType)
        return this
    }

    override fun addJson(json: String?): PostMulBuilderImpl {
        return super.addJson(json)
    }
    override fun autoCancel(activity: Activity?): PostMulGenerator {
        return this
    }
}