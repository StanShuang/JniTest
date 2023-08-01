package com.example.stan.jnitest.utils

import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.GlobalScope.coroutineContext
import kotlin.coroutines.CoroutineContext

/**
 *@Author Stan
 *@Description 协程练习
 *@Date 2023/4/3 14:30
 */
object CoroutineTest {
    private const val tag = "JniTest::CoroutineTest"

    /**
     * 使用官方库的 MainScope()获取一个协程作用域用于创建协程
     */
    private val mScope = MainScope()

    /**
     * 1.Suspend function 挂起函数  Suspend函数 ---> 编译器封装成Continuation ---> resumeWith(result:Result<T>) ---> Result.isFailure/isSuccess。
     * 2.创建协程：
     *      CoroutineScope.lunch():launch 构建器适合执行 "一劳永逸" 的工作，意思就是说它可以启动新协程而不将结果返回给调用方。
     *      CoroutineScope.async():async 构建器可启动新协程并允许您使用一个名为 await 的挂起函数返回 result;配合await使用，很好的处理并发问题。
     * 3.launch函数的定义，它以CoroutineScope的扩展函数的形成出现，函数参数分别是:协程上下文CoroutineContext、协程启动模式CoroutineStart、协程体；
     * 其中CoroutineContext又包括了Job、CoroutineDispatcher、CoroutineName。
     * 4.[CoroutineContext] - 协程上下文：是 Kotlin 协程的一个基本结构单元。巧妙的运用协程上下文是至关重要的，以此来实现正确的线程行为、生命周期、异常以及调试。
     *                       它包含用户定义的一些数据集合，这些数据与协程密切相关。它是一个有索引的 Element 实例集合。这个有索引的集合类似于一个介于 set 和 map之间的数据结构。
     *                       每个 element在这个集合有一个唯一的 Key 。当多个 element 的 key 的引用相同，则代表属于集合里同一个 element。
     *                       它由如下几项构成:
     *                       Job:控制协程的生命周期。
     *                       CoroutineDispatcher:向合适的线程分发任务。
     *                       CoroutineName:协程的名称,调试的时候使用。
     *                       CoroutineExceptionHandler:处理未被捕获的异常。
     *
     *                       CoroutineContext 有两个非常重要的元素 — Job 和 Dispatcher，Job 是当前的 Coroutine 实例而 Dispatcher 决定了当前 Coroutine 执行的线程，
     *                       还可以添加CoroutineName，用于调试，添加 CoroutineExceptionHandler 用于捕获异常，它们都实现了Element接口
     * 5.[SupervisorJob]:SupervisorJob 是一个顶层函数,该函数创建了一个处于 active 状态的supervisor job。
     *                   新的协程被创建时，会生成新的 Job 实例替代 SupervisorJob。
     * 6.[CoroutineDispatcher] - 调度器:CoroutineDispatcher 定义了 Coroutine 执行的线程。CoroutineDispatcher 可以限定 Coroutine 在某一个线程执行、
     *                          也可以分配到一个线程池来执行、也可以不限制其执行的线程。
     *                           [Dispatchers.Default]、[Dispatchers.IO]、[Dispatchers.Unconfined]、[Dispatchers.Main]
     * 7.[CoroutineStart] - 协程启动模式: 这些启动模式的设计主要是为了应对某些特殊的场景。业务开发实践中通常使用DEFAULT和LAZY这两个启动模式就够了
     * 8.[CoroutineScope] - 协程作用域: CoroutineScope 只是定义了一个新 Coroutine 的执行 Scope。每个 coroutine builder 都是 CoroutineScope 的扩展函数，并且自动的继承了当前 Scope 的 coroutineContext 。
     *                                  父协程被取消，则所有子协程均被取消。由于协同作用域和主从作用域中都存在父子协程关系，因此此条规则都适用。
    父协程需要等待子协程执行完毕之后才会最终进入完成状态，不管父协程自身的协程体是否已经执行完。
    子协程会继承父协程的协程上下文中的元素，如果自身有相同key的成员，则覆盖对应的key，覆盖的效果仅限自身范围内有效。
     *                      GlobalScope - 不推荐使用
     *                      runBlocking{} - 主要用于测试: 该协程会阻塞当前线程直到协程体执行完成。
     *                      MainScope() - 可用于开发: 该函数是一个顶层函数，用于返回一个上下文是SupervisorJob() + Dispatchers.Main的作用域，该作用域常被使用在Activity/Fragment，并且在界面销毁时要调用fun
     *                                     CoroutineScope.cancel(cause: CancellationException? = null)对协程进行取消
     *                      LifecycleOwner.lifecycleScope - 推荐使用: 它与LifecycleOwner的Lifecycle绑定，Lifecycle被销毁时，此作用域将被取消
     *                      ViewModel.viewModelScope - 推荐使用: 它是ViewModel的扩展属性，它能够在此ViewModel销毁时自动取消，同样不会造成协程泄漏。
     *                                               该扩展属性返回的作用域的上下文同样是SupervisorJob() + Dispatchers.Main.immediate
     *                      coroutineScope & supervisorScope: 两个函数都是挂起函数，需要运行在协程内或挂起函数内.
     *                                                        coroutineScope 内部的异常会向上传播，子协程未捕获的异常会向上传递给父协程，任何一个子协程异常退出，会导致整体的退出；
     *                                                        supervisorScope 内部的异常不会向上传播，一个子协程异常退出，不会影响父协程和兄弟协程的运行。
     *
     *9.协程的取消和异常: 普通协程如果产生未处理异常会将此异常传播至它的父协程，然后父协程会取消所有的子协程、取消自己、将异常继续向上传递。
     *
     */
    fun test1() {
        GlobalScope.launch {
            val arg1 = sunpendF1()
            val arg2 = sunpendF2()
            Log.d(tag, "suspend finish arg1:$arg1 arg2:$arg2 result:${arg1 + arg2}")
        }
    }

    fun testCoroutineCancel() {
        mScope.launch {
            delay(500)
            Log.e(tag, "Child 1")
        }

        mScope.launch(Dispatchers.Default + CoroutineExceptionHandler { _, throwable ->
            Log.e(tag, "CoroutineExceptionHandler: $throwable")
        }) {
            delay(1000)
            Log.e(tag, "Child 2")
            throw RuntimeException("--> RuntimeException <--")
        }

        mScope.launch(Dispatchers.Default) {
            delay(1500)
            Log.e(tag, "Child 3")
        }
    }

    fun startForLunch() {
        //创建一个默认参数的协程，其默认的调度模式为Main 也就是说该协程的线程环境是Main线程
        val job1 = mScope.launch {
            //协程体

            // 延迟1000毫秒  delay是一个挂起函数
            // 在这1000毫秒内该协程所处的线程不会阻塞
            // 协程将线程的执行权交出去，该线程该干嘛干嘛，到时间后会恢复至此继续向下执行
            delay(1000)
        }
        // 创建一个指定了调度模式的协程，该协程的运行线程为IO线程
        val job2 = mScope.launch(Dispatchers.IO) {
            // 此处是IO线程模式

            // 切线程 将协程所处的线程环境切至指定的调度模式Main
            withContext(Dispatchers.Main) {
                // 现在这里就是Main线程了  可以在此进行UI操作了
            }
        }
        mScope.launch(Dispatchers.IO) {
            // 执行getUserInfo方法时会将线程切至IO去执行
            val userInfo = getUserInfo()
            withContext(Dispatchers.Main) {
                // 现在这里就是Main线程了  可以在此进行UI操作了
            }
        }
    }

    fun startForAsync() {
        mScope.launch {
            // 开启一个IO模式的线程 并返回一个Deferred，Deferred可以用来获取返回值
            // 代码执行到此处时会新开一个协程 然后去执行协程体  父协程的代码会接着往下走
            Log.d(tag,"async start.")
            val deferred = async(Dispatchers.IO) {
                Log.d(tag,"async1 start.")
                // 模拟耗时
                delay(2000)
                // 返回一个值
                "returnValue"
            }
            val deferred2 = async(Dispatchers.IO) {
                Log.d(tag,"async2 start.")
                // 模拟耗时
                delay(500)
                // 返回一个值
                "returnValue"
            }
            // 等待async执行完成获取返回值 此处并不会阻塞线程  而是挂起 将线程的执行权交出去
            // 等到async的协程体执行完毕后  会恢复协程继续往下执行
            val date = deferred.await()
            val date2 = deferred2.await()
            Log.d(tag,"async end.")
        }
    }

    private suspend fun getUserInfo(): String {
        return withContext(Dispatchers.IO) {
            delay(2000)
            "Kotlin"
        }
    }

    suspend fun sunpendF1(): Int {
        delay(1000)
        Log.d(tag, "suspend fun 1")
        return 2
    }

    suspend fun sunpendF2(): Int {
        delay(1000)
        Log.d(tag, "suspend fun 2")
        return 4
    }
}