package com.example.stan.jnitest.mvvm.view

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.example.stan.jnitest.mvvm.viewmodel.ViewBehavior

/**
 *@Author Stan
 *@Description
 *@Date 2023/4/4 14:11
 */
abstract class BaseBindingActivity<B : ViewDataBinding> : AppCompatActivity(), ViewBehavior {
    protected lateinit var binding: B
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectDataBinding()
        init(savedInstanceState)
    }

    protected open fun injectDataBinding() {
        binding = DataBindingUtil.setContentView(this, getLayoutId())
        binding.lifecycleOwner = this
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.unbind()
    }

    @LayoutRes
    protected abstract fun getLayoutId(): Int

    /**
     *  初始化操作
     */
    protected abstract fun init(savedInstanceState: Bundle?)
}