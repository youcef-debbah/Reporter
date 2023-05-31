package dz.nexatech.reporter.util.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dz.nexatech.reporter.client.R
import dz.nexatech.reporter.util.model.AppConfig
import dz.nexatech.reporter.util.model.RemoteConfig
import dz.nexatech.reporter.util.model.rememberMaxLayoutColumnWidth

@Composable
fun <T> AnimatedLazyLoading(
    animationEnabled: RemoteConfig<Boolean>,
    data: T,
    loadingContent: @Composable () -> Unit = {
        val width by rememberMaxLayoutColumnWidth()
        PaddedBox(Modifier.requiredWidth(width)) {
            PaddedColumn {
                ThemedText(R.string.content_loading_desc, Modifier.contentPadding())
                CircularProgressIndicator(
                    Modifier
                        .contentPadding()
                        .size(24.dp)
                )
            }
        }
    },
    content: @Composable (T) -> Unit
) {
    if (AppConfig.get(animationEnabled)) {
        CentredColumn {
            AnimatedVisibility(data == null) {
                loadingContent()
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