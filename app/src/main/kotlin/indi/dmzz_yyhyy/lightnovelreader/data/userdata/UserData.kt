package indi.dmzz_yyhyy.lightnovelreader.data.userdata

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

abstract class UserData<T> (
    open val path: String
) {
    val group get() = path.split(".").dropLast(1).joinToString(".")
    /**
     * 此函数为阻塞函数，请务必不要在初始化阶段或主线程上调用
     */
    abstract fun set(value: T)
    fun asynchronousSet(value: T) {
        CoroutineScope(Dispatchers.IO).launch {
            set(value)
        }
    }
    /**
     * 此函数为阻塞函数，请务必不要在初始化阶段或主线程上调用
     */
    abstract fun get(): T?
    abstract fun getFlow(): Flow<T?>
    fun getFlowWithDefault(default: T): Flow<T> = getFlow().map { it ?: default }
    /**
     * 此函数为阻塞函数，请务必不要在初始化阶段或主线程上调用
     */
    fun getOrDefault(default: T): T {
        return get() ?: default
    }
    /**
     * 此函数为阻塞函数，请务必不要在初始化阶段或主线程上调用
     */
    fun update(updater: (T) -> T, default: T) {
        set(updater(getOrDefault(default)))
    }
}