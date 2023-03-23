package com.reporter.common

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

@PublicAPI
fun mainLaunch(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
): Job = mainScope().launch(context, start, block)

@PublicAPI
fun ioLaunch(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
): Job = ioScope().launch(context, start, block)

@PublicAPI
fun backgroundLaunch(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
): Job = backgroundScope().launch(context, start, block)

@PublicAPI
fun <T> mainAsync(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> T
): Deferred<T> = mainScope().async(context, start, block)

@PublicAPI
fun <T> ioAsync(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> T
): Deferred<T> = ioScope().async(context, start, block)

@PublicAPI
fun <T> backgroundAsync(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> T
): Deferred<T> = backgroundScope().async(context, start, block)

@PublicAPI
suspend fun <T> withMain(block: suspend CoroutineScope.() -> T): T =
    withContext(AsyncConfig.mainDispatcher, block)

@PublicAPI
suspend fun <T> withIO(block: suspend CoroutineScope.() -> T): T =
    withContext(AsyncConfig.ioDispatcher, block)

@PublicAPI
suspend fun <T> withBackground(block: suspend CoroutineScope.() -> T): T =
    withContext(AsyncConfig.backgroundDispatcher, block)

fun mainScope() = CoroutineScope(SupervisorJob() + AsyncConfig.mainDispatcher)

fun ioScope() = CoroutineScope(SupervisorJob() + AsyncConfig.ioDispatcher)

fun backgroundScope() = CoroutineScope(SupervisorJob() + AsyncConfig.backgroundDispatcher)

object AsyncConfig {

    @Volatile
    var mainDispatcher = Dispatchers.Main

    @Volatile
    var ioDispatcher = Dispatchers.IO

    @Volatile
    var backgroundDispatcher = Dispatchers.Default

}