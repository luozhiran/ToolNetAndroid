package com.itg.net.reqeust.model.get

import com.itg.net.base.Builder

interface GetBuilder:Builder {
    fun addParam(map:MutableMap<String,String?>?): Builder
    fun addParam(key: String?, value: String?): Builder
}