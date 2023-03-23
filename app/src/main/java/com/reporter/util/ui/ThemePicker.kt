package com.reporter.util.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import com.reporter.client.R

@Composable
fun ThemePicker(
    selection: String,
    modifier: Modifier = Modifier,
    title: Int = R.string.select_theme,
    darkColors: Boolean = isSystemInDarkTheme(),
    onThemeSelected: (ThemeColors?) -> Unit,
) {
    PaddedColumn(modifier) {

        ThemedText(
            title,
            style = Theme.typography.titleLarge,
            modifier = Modifier.contentPadding(),
        )

        Divider(Modifier.contentPadding())

        CentredColumn(
            Modifier.padding(
                top = Theme.dimens.content_padding.top * 2,
                bottom = Theme.dimens.content_padding.bottom * 2,
                start = zero_padding,
                end = zero_padding,
            )
        ) {
            FlowRow(mainAxisAlignment = FlowMainAxisAlignment.Center) {
                for (theme in ThemeColors.values()) {
                    val themeName = theme.name
                    key(themeName) {
                        TextButton(onClick = { onThemeSelected.invoke(theme) }) {
                            ThemePreviewIcon(selection, themeName, theme.colorScheme(darkColors))
                        }
                    }
                }
            }

            CentredRow {
                TextButton(onClick = { onThemeSelected.invoke(null) }) {
                    ThemePreviewIcon(selection, "", Theme.colorScheme)
                    ThemedText(R.string.default_theme)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ThemePreviewIconPreview() {
    PaddedRow {
        ThemePreviewIcon("", "", ThemeColors.GREEN.colorScheme(false))
        ThemePreviewIcon("", " ", ThemeColors.RED.colorScheme(false))
    }
}

@Composable
private fun ThemePreviewIcon(
    selection: String,
    themeName: String,
    themeColorScheme: ColorScheme
) {
    val shape = CircleShape
    VectorIcon(
        icon = R.drawable.baseline_format_color_text_24,
        desc = null,
        tint = themeColorScheme.onPrimaryContainer,
        modifier = Modifier
            .let {
                if (selection == themeName) {
                    it
                        .border(small_padding, Theme.colorScheme.outline, shape)
                        .padding(small_padding)
                        .border(small_padding, Theme.colorScheme.surface, shape)
                        .padding(small_padding)
                } else {
                    it
                        .padding(small_padding * 2)
                }
            }
            .background(themeColorScheme.primaryContainer, shape)
            .padding(large_padding)
    )
}