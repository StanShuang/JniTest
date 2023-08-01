package com.example.stan.jnitest.mvvm.test

import com.example.stan.jnitest.mvvm.viewmodel.BaseViewModel

/**
 *@Author Stan
 *@Description
 *@Date 2023/4/4 16:37
 */
class MVVMTestViewModel:BaseViewModel() {
    fun getDate(){
        launchOnUi {
            HttpRequestUtils.getRequestDate(){

            }.doSuccess {
                println(">>>>> 第一次")
            }.doFailure { code, msg ->

            }.procceed()

            HttpRequestUtils.getRequestDate(){

            }.doSuccess {
                println(">>>>> 第二次")
            }.doFailure { code, msg ->

            }.procceed()
        }
    }

}