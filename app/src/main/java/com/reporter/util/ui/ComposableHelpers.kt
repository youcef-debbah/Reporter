package com.reporter.util.ui

import androidx.annotation.DimenRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlin.coroutines.EmptyCoroutineContext

@Composable
@ReadOnlyComposable
fun stringRes(@StringRes id: Int): String = stringResource(id)

@Composable
@ReadOnlyComposable
fun dimen(@DimenRes id: Int): Dp = dimensionResource(id)

@Composable
fun <T> Flow<T>.collectWithLifecycleAsState(
    initial: T,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    context: EmptyCoroutineContext = EmptyCoroutineContext,
): State<T> = produceState(initial, this, lifecycleOwner, minActiveState, context) {
    lifecycleOwner.repeatOnLifecycle(minActiveState) {
        if (context == EmptyCoroutineContext) {
            collect { value = it }
        } else withContext(context) {
            collect { value = it }
        }
    }
}