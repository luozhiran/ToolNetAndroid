package com.itg.net.reqeust

import com.itg.net.DdNet
import com.itg.net.ModeType
import com.itg.net.reqeust.model.get.Get
import com.itg.net.reqeust.model.post.multipart.PostMul
import com.itg.net.reqeust.model.params.ParamsBuilder
import com.itg.net.reqeust.model.post.content.PostContent
import com.itg.net.reqeust.model.post.file.PostFile
import com.itg.net.reqeust.model.post.file.PostResumeFile
import com.itg.net.reqeust.model.post.form.PostForm
import com.itg.net.reqeust.model.post.json.PostJson

fun create(type: ModeType): ParamsBuilder? {
    var adapterBuilder: ParamsBuilder? = null
    adapterBuilder = when (type) {
        ModeType.Get -> Get()
        ModeType.PostMul -> PostMul()
        ModeType.PostJson->PostJson()
        ModeType.PostForm->PostForm()
        ModeType.PostFile-> PostFile()
        ModeType.PostContent->PostContent()
        ModeType.PostResume->PostResumeFile()
        else-> null
    }
    return adapterBuilder
}