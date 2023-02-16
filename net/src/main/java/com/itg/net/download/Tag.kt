package com.itg.net.download


const val CANCEL_TASK = 1
const val DOWNLOAD_TASK = 2


const val DOWNLOAD_FILE = 1
const val DOWNLOAD_SUCCESS = 2

const val ERROR_TAG_1 = "不支持断点续传"
const val ERROR_TAG_2 = "创建文件夹失败"
const val ERROR_TAG_3 = "下载任务被主动取消"
const val ERROR_TAG_4 = "md5校验失败,并删除校验失败文件"
const val ERROR_TAG_5 = "下载任务失败，重命名失败"
const val ERROR_TAG_6 = "response.body is null"
const val ERROR_TAG_7 = "FileNotFoundException: not found file exception"
const val ERROR_TAG_8 = "IOException: io exception"
const val ERROR_TAG_9 = "response.code() not is 200"