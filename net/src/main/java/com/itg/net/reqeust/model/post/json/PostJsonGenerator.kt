package com.itg.net.reqeust.model.post.json

import com.itg.net.reqeust.model.params.ParamsBuilder
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

abstract class PostJsonGenerator:PostJsonBuilder() {

    fun addJson(json:String?): ParamsBuilder = addJson1(json)


}