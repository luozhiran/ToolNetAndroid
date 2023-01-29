package com.itg.net.reqeust

import com.itg.net.DdNet
import com.itg.net.reqeust.model.Delete
import com.itg.net.reqeust.model.Get
import com.itg.net.reqeust.model.Post
import com.itg.net.reqeust.model.Put

fun create(type: Int): AdapterBuilder? {
    var adapterBuilder: AdapterBuilder? = null
    adapterBuilder = when (type) {
        DdNet.GET -> Get()
        DdNet.POST -> Post()
        DdNet.DELETE -> Delete()
        DdNet.PUT -> Put()
        else-> null
    }
    return adapterBuilder
}