package com.example.stan.jnitest.mvvm.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment

/**
 *@Author Stan
 *@Description
 *@Date 2023/4/4 14:53
 */
abstract class BaseBindingFragment<B : ViewDataBinding> : Fragment() {
    /**
     * 缓存视图，如果视图已经创建，则不再初始化视图
     */
    private var rootView: View? = null
    protected lateinit var binding: B

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (rootView != null) {
            return rootView
        }
        rootView = inflater.inflate(getLayoutId(), container, false)
        injectDataBinding(inflater, container)
        init(savedInstanceState)
        return rootView

    }

    private fun injectDataBinding(inflater: LayoutInflater, container: ViewGroup?) {
        binding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
        binding.lifecycleOwner = this
        rootView = binding.root
    }

    override fun onDestroy() {
        binding.unbind()
        super.onDestroy()
    }

    /**
     *  初始化操作
     */
    abstract fun init(savedInstanceState: Bundle?)

    @LayoutRes
    protected abstract fun getLayoutId(): Int
}