package com.example.stan.jnitest.mvvm.view

import android.os.Bundle
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.example.stan.jnitest.mvvm.viewmodel.BaseViewModel
import com.example.stan.jnitest.mvvm.viewmodel.ViewBehavior

/**
 *@Author Stan
 *@Description
 *@Date 2023/4/4 14:59
 */
abstract class BaseBVMFragment<B : ViewDataBinding, VM : BaseViewModel> : BaseBindingFragment<B>(),
    ViewBehavior {
    protected lateinit var viewModel: VM
        private set

    override fun init(savedInstanceState: Bundle?) {
        injectViewModel()
        initialize(savedInstanceState)
        initInternalObserver()
    }

    private fun initInternalObserver() {
        viewModel._loadingEvent.observe(this) {
            showLoadingUI(it)
        }

    }

    private fun injectViewModel() {
        val vm = crateViewModel()
        viewModel =
            ViewModelProvider(this, BaseViewModel.createViewModelFactory(vm))[vm::class.java]
        viewModel.application = activity!!.application
        lifecycle.addObserver(viewModel)
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(viewModel)
    }

    abstract fun crateViewModel(): VM

    abstract fun initialize(savedInstanceState: Bundle?)
}