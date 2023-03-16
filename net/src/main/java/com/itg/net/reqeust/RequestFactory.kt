package com.itg.net.reqeust

import com.itg.net.DdNet
import com.itg.net.reqeust.model.get.Get
import com.itg.net.reqeust.model.post.Post
import com.itg.net.reqeust.model.params.ParamsBuilder

fun create(type: Int): ParamsBuilder? {
    var adapterBuilder: ParamsBuilder? = null
    adapterBuilder = when (type) {
        DdNet.GET -> Get()
        DdNet.POST -> Post()
        else-> null
    }
    return adapterBuilder
}