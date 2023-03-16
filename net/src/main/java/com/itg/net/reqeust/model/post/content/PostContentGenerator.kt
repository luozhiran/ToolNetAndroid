package com.itg.net.reqeust.model.post.content

import com.itg.net.reqeust.model.params.ParamsBuilder

abstract class PostContentGenerator : PostContentBuilder() {

    fun addContent(content: String?, mediaType: String?): ParamsBuilder =
        super.addContent1(content, mediaType)

    fun addContent(content: String?, contentFlag: String?, mediaType: String?): ParamsBuilder =
        super.addContent1(content, contentFlag, mediaType)


}