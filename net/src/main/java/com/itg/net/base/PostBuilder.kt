package com.itg.net.base

import com.itg.net.reqeust.castration.IntervalFileCastrationBuilder
import com.itg.net.reqeust.castration.JsonCastrationBuilder
import java.io.File

/**
 * Post特有的参数
 */
interface PostBuilder : Builder{
    fun addFile(file: File?): Builder
    fun addFile(fileName: String?, file: File?): Builder
    fun addFile(fileName: String?, mediaType: String?, file: File?): Builder
    fun addContent(content: String?, mediaType: String?): Builder
    fun addContent(content: String?, contentFlag: String?, mediaType: String?): Builder
    fun addJson(content: String?): JsonCastrationBuilder
    fun addInterval(file: File?, offset: Long): IntervalFileCastrationBuilder
}