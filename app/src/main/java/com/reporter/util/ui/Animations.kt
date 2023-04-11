package com.reporter.util.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.reporter.client.R
import com.reporter.util.model.AppConfig
import com.reporter.util.model.RemoteConfig

@Composable
fun <T> AnimatedLazyLoading(
    animationEnabled: RemoteConfig<Boolean>,
    data: T,
    loadingContent: @Composable () -> Unit = {
        ThemedText(R.string.content_loading_desc)
        CircularProgressIndicator(modifier = Modifier.size(24.dp))
    },
    content: @Composable (T) -> Unit
) {
    if (AppConfig.get(animationEnabled)) {
        Column {
            AnimatedVisibility(data == null) {
                PaddedColumn(Modifier.contentPadding()) {
                    loadingContent()
                }
            }
            AnimatedVisibility(data != null) {
                PaddedColumn {
                    content(data)
                }
            }
        }
    } else {
        content(data)
    }
}