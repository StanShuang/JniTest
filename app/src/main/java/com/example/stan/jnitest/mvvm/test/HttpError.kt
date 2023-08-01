package com.example.stan.jnitest.mvvm.test

import androidx.annotation.StringRes
import com.example.stan.jnitest.R

/**
 *@Author Stan
 *@Description
 *@Date 2023/4/4 17:59
 */
enum class HttpError(val code: Int, val message: String) {
    // 未知错误
    UNKNOWN(-1, "http_error_unknow"),

    // 网络连接错误
    CONNECT_ERROR(-2, "http_error_connect"),

    // 连接超时
    CONNECT_TIMEOUT(-3, "http_error_connect_timeout"),

    // 错误的请求
    BAD_NETWORK(-4, "http_error_bad_network"),

    // 数据解析错误
    PARSE_ERROR(-5, "http_error_parse"),

    // 取消请求
    CANCEL_REQUEST(-6, "http_cancel_request"),
}