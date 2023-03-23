package com.reporter.util.model

import android.content.Context
import android.os.Looper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.reporter.common.PublicAPI
import com.reporter.common.mainLaunch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun Context?.asLifecycleOwner(): LifecycleOwner =
    if (this is LifecycleOwner) this else ProcessLifecycleOwner.get()

fun LifecycleOwner.whileLifecycle(
    state: Lifecycle.State,
    block: suspend CoroutineScope.() -> Unit
) = lifecycleScope.launch {
    lifecycle.repeatOnLifecycle(state, block)
}

fun LifecycleOwner.whileStarted(block: suspend CoroutineScope.() -> Unit) =
    whileLifecycle(Lifecycle.State.STARTED, block)

fun LifecycleOwner.whileResumed(block: suspend CoroutineScope.() -> Unit) =
    whileLifecycle(Lifecycle.State.RESUMED, block)

fun LifecycleOwner.whileCreated(block: suspend CoroutineScope.() -> Unit) =
    whileLifecycle(Lifecycle.State.CREATED, block)

@PublicAPI
fun onMain(block: () -> Unit): Unit {
    if (Looper.myLooper() == Looper.getMainLooper()) {
        block()
    } else {
        mainLaunch { block() }
    }
}