package dz.nexatech.reporter.client.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

fun CoroutineScope.mainLaunch(
    block: suspend CoroutineScope.() -> Unit
): Job =
    this.launch(AsyncConfig.mainDispatcher, CoroutineStart.DEFAULT, block)

fun CoroutineScope.ioLaunch(
    block: suspend CoroutineScope.() -> Unit
): Job =
    this.launch(AsyncConfig.ioDispatcher, CoroutineStart.DEFAULT, block)

fun CoroutineScope.backgroundLaunch(
    block: suspend CoroutineScope.() -> Unit
): Job =
    this.launch(AsyncConfig.backgroundDispatcher, CoroutineStart.DEFAULT, block)

fun mainLaunch(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
): Job = mainScope().launch(context, start, block)

fun ioLaunch(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
): Job = ioScope().launch(context, start, block)

fun backgroundLaunch(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
): Job = backgroundScope().launch(context, start, block)

fun <T> mainAsync(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> T
): Deferred<T> = mainScope().async(context, start, block)

fun <T> ioAsync(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> T
): Deferred<T> = ioScope().async(context, start, block)

fun <T> backgroundAsync(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> T
): Deferred<T> = backgroundScope().async(context, start, block)

suspend fun <T> withMain(block: suspend CoroutineScope.() -> T): T =
    withContext(AsyncConfig.mainDispatcher, block)

suspend fun <T> withIO(block: suspend CoroutineScope.() -> T): T =
    withContext(AsyncConfig.ioDispatcher, block)

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