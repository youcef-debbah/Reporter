package com.reporter.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import java.util.concurrent.atomic.AtomicReference
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

class LazyValue<T>(
    private val context: CoroutineContext = EmptyCoroutineContext,
    private val start: CoroutineStart = CoroutineStart.DEFAULT,
    private val block: suspend CoroutineScope.() -> T,
) {
    private val value = AtomicReference<Deferred<T>?>()

    suspend operator fun invoke(): T = (value.get() ?: getAsync()).await()

    private fun getAsync() = value.updateAndGet { actual ->
        actual ?: ioScope().async(context, start, block)
    }!!

    suspend fun get(): T = invoke()

    suspend fun init() {
        getAsync().await()
    }

    fun isLoaded() = value.get()?.isCompleted ?: false
}