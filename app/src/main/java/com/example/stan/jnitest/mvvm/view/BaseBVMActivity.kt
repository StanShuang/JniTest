package com.example.stan.jnitest.mvvm.view

import android.os.Bundle
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.example.stan.jnitest.mvvm.viewmodel.BaseViewModel
import com.example.stan.jnitest.mvvm.viewmodel.ViewBehavior

/**
 *@Author Stan
 *@Description
 *@Date 2023/4/4 14:24
 */
abstract class BaseBVMActivity<B : ViewDataBinding, VM : BaseViewModel> : BaseBindingActivity<B>(),
    ViewBehavior {
    protected lateinit var viewModel: VM

    override fun init(savedInstanceState: Bundle?) {
        injectViewModel()
        initialize(savedInstanceState)
        initInternalObserver()
    }

    private fun injectViewModel() {
        val vm = crateViewModel()
        viewModel =
            ViewModelProvider(this, BaseViewModel.createViewModelFactory(vm)).get(vm::class.java)
        viewModel.application = application
        lifecycle.addObserver(viewModel)
    }

    private fun initInternalObserver() {
        viewModel._loadingEvent.observe(this) {
            showLoadingUI(it)
        }
    }

    fun getActivityViewModel(): VM {
        return viewModel
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.unbind()
        lifecycle.removeObserver(viewModel)
    }

    abstract fun crateViewModel(): VM

    abstract fun initialize(savedInstanceState: Bundle?)
}