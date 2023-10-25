package com.itg.net.tools

import okhttp3.Cookie
import java.lang.StringBuilder


object StrTools {

   private const val EQUAL_SIGN_TAG = "="

    @JvmStatic
    fun getCookieString(cookie: List<Cookie?>?):String?{
        if (cookie.isNullOrEmpty()) return null
        val cookieHeader = StringBuilder()
        cookie.forEachIndexed { index, value ->
            if (index > 0) {
                cookieHeader.append("; ")
            }
            if (value != null) {
                cookieHeader.append(value.name).append(EQUAL_SIGN_TAG).append(value.value)
            }
        }
        return cookieHeader.toString()
    }
}