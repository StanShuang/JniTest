package com.example.stan.jnitest.mvvm.view

import android.content.Intent
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.databinding.ViewDataBinding
import com.example.stan.jnitest.mvvm.viewmodel.BaseViewModel

/**
 *@Author Stan
 *@Description
 *@Date 2023/4/4 16:55
 */
abstract class BaseAppBVMActivity<B : ViewDataBinding, VM : BaseViewModel> :
    BaseBVMActivity<B, VM>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun showLoadingUI(isShow: Boolean) {

    }

    override fun showEmptyUI(isShow: Boolean) {

    }

    override fun navigate(page: Any) {
        startActivity(Intent(this, page as Class<*>))
    }

    override fun backPress(arg: Any?) {
        onBackPressed()
    }

    override fun finishPage(arg: Any?) {
        finish()
    }

}