package com.example.stan.jnitest.mvvm.test

import android.os.Bundle
import com.example.stan.jnitest.R
import com.example.stan.jnitest.databinding.ActivityMvvmtestBinding
import com.example.stan.jnitest.mvvm.view.BaseAppBVMActivity
import com.example.stan.jnitest.mvvm.view.BaseBVMActivity

/**
 *@Author Stan
 *@Description
 *@Date 2023/4/4 16:33
 */
class MVVMTestActivity : BaseAppBVMActivity<ActivityMvvmtestBinding, MVVMTestViewModel>() {
    override fun crateViewModel(): MVVMTestViewModel {
        return MVVMTestViewModel();
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_mvvmtest
    }

    override fun initialize(savedInstanceState: Bundle?) {
        viewModel.getDate()
    }

}