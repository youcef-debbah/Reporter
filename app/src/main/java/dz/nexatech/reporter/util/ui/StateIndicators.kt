package dz.nexatech.reporter.util.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import dz.nexatech.reporter.client.R

@Preview(showBackground = true)
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
            InfoIcon(
                icon = R.drawable.round_signal_wifi_bad_24,
                desc = R.string.icon_xdesc_bad_wifi_signal,
                modifier = Modifier.contentPadding(),
            )
            ThemedText(
                text = stringRes(R.string.error_while_loading_content),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Start,
            )
            ThemedLink(
                text = stringRes(R.string.retry),
                onClick = onRetry,
                icon = R.drawable.baseline_refresh_24,
                desc = R.string.icon_xdesc_refresh,
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
        InfoIcon(
            icon = R.drawable.baseline_search_off_24,
            desc = R.string.icon_xdesc_refresh,
            modifier = Modifier.contentPadding(),
            tint = color,
        )
        ThemedText(
            text = stringRes(R.string.no_result_found),
            color = color,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Start,
        )
        Button(
            onClick = onRetry,
        ) {
            InfoIcon(
                icon = R.drawable.baseline_refresh_24,
                desc = R.string.icon_xdesc_no_search_result,
            )
            ThemedText(
                text = stringRes(R.string.retry),
            )
        }
    }
//    PaddedColumn {
//        CentredRow {
//            VectorIcon(
//                icon = R.drawable.baseline_search_off_24,
//                desc = R.string.icon_xdesc_refresh,
//            )
//            ThemedText(
//                text = stringRes(R.string.no_result_found),
//                style = Theme.typography.titleSmall,
//            )
//        }
//        ThemedButton(
//            onClick = onRetry,
//        ) {
//            VectorIcon(
//                icon = R.drawable.baseline_refresh_24,
//                desc = R.string.icon_xdesc_no_search_result,
//            )
//            ThemedText(
//                text = stringRes(R.string.retry),
//            )
//        }
//    }
}