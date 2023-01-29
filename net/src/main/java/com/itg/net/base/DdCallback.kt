package com.itg.net.base

interface DdCallback {
    fun onFailure(er: String?)

    fun onResponse(result: String?, code: Int)
}