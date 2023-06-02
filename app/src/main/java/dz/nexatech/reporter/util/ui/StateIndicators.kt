package dz.nexatech.reporter.util.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import dz.nexatech.reporter.client.R

//@Preview(showBackground = true)
@Composable
private fun Preview() {
    NoResultFound({})
}

@Composable
fun LoadingErrorCard(onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Theme.colorScheme.errorContainer)
    ) {
        PaddedRow(Modifier.fillMaxWidth()) {
            DecorativeIcon(
                icon = R.drawable.round_signal_wifi_bad_24,
                modifier = Modifier.contentPadding(),
            )
            Body(
                text = stringRes(R.string.error_while_loading_content),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Start,
            )
            ThemedLink(
                text = stringRes(R.string.retry),
                onClick = onRetry,
                icon = R.drawable.baseline_refresh_24,
            )
        }
    }
}

@Composable
fun NoResultFound(onRetry: () -> Unit, modifier: Modifier = Modifier) {
    val color = Theme.colorScheme.onSurface.copy(alpha = 0.7f)
    CentredRow(
        modifier
            .fillMaxWidth()
            .contentPadding(start = zero_padding, end = zero_padding)
    ) {
        DecorativeIcon(
            icon = R.drawable.baseline_search_off_24,
            modifier = Modifier.contentPadding(),
            tint = color,
        )
        Body(
            text = stringRes(R.string.no_result_found),
            color = color,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Start,
        )
        Button(
            onClick = onRetry,
        ) {
            DecorativeIcon(
                icon = R.drawable.baseline_refresh_24,
            )
            Body(
                text = stringRes(R.string.retry),
            )
        }
    }
}