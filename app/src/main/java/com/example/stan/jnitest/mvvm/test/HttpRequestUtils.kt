package com.example.stan.jnitest.mvvm.test

import kotlinx.coroutines.delay

/**
 *@Author Stan
 *@Description
 *@Date 2023/4/6 10:01
 */
object HttpRequestUtils {
    suspend fun getRequestDate(function: () -> Unit): ParseResult<String> {
        //模仿网络请求
        delay(2000)
//        return ParseResult.Failure(1,"http error")
        return ParseResult.Success("this is return data")
    }

}