package indi.dmzz_yyhyy.lightnovelreader.data.userdata

import kotlinx.coroutines.flow.Flow

abstract class UserData<T> (
    open val path: String
) {
    val group get() = path.split(".").dropLast(1).joinToString(".")
    /**
     * 此函数为阻塞函数，请务必不要在初始化阶段或主线程上调用
     */
    abstract fun set(value: T)
    /**
     * 此函数为阻塞函数，请务必不要在初始化阶段或主线程上调用
     */
    abstract fun get(): T?
    abstract fun getFlow(): Flow<T?>
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