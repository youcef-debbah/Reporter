package dz.nexatech.reporter.util.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dz.nexatech.reporter.client.R
import dz.nexatech.reporter.util.model.APPLICATION_THEME

val themePreviewSize = 60.dp
val themePreviewPadding = padding_unit * 6
val themePreviewShape = CircleShape

@Composable
private fun ThemePreviewIcon(
    surfaceColor: Color,
    outlineColor: Color,
    tint: Color,
    background: Color,
    iconShape: Shape,
    modifier: Modifier = Modifier,
) {
    InfoIcon(
        icon = R.drawable.baseline_format_color_text_24,
        desc = null,
        tint = tint,
        modifier = modifier
            .border(small_padding, outlineColor, iconShape)
            .padding(small_padding)
            .border(small_padding, surfaceColor, iconShape)
            .padding(small_padding)
            .background(background, iconShape)
            .padding(themePreviewPadding)
            .size(themePreviewSize)
    )
}

@ExperimentalLayoutApi
@Composable
fun ThemePicker(
    selectedTheme: String,
    modifier: Modifier = Modifier,
    title: Int = R.string.select_theme,
    isDarkColors: Boolean = isSystemInDarkTheme(),
    enabled: Boolean = true,
    header: @Composable () -> Unit = {
        Title(
            textRes = title,
            modifier = Modifier.contentPadding(),
            style = Theme.typography.titleLarge,
        )

        PaddedDivider()
    },
    footer: @Composable () -> Unit = {},
    onThemeSelected: (ThemeColors) -> Unit,
) {
    val currentColorScheme = Theme.colorScheme
    val surfaceColor = Color.Transparent

    PaddedColumn {
        header()
        FlowRow(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            for (theme in ThemeColors.values()) {
                key(theme.name) {
                    val colorScheme = theme.colorScheme(isDarkColors)
                    val outlineColor = if (selectedTheme == theme.name) {
                        currentColorScheme.outline.disabled(enabled.not())
                    } else {
                        surfaceColor
                    }
                    ThemeIconButton(
                        enabled = enabled,
                        themeColorScheme = colorScheme,
                        surfaceColor = surfaceColor,
                        outlineColor = outlineColor,
                        onClick = { onThemeSelected.invoke(theme) },
                    )
                }
            }
        }
        footer()
    }
}

@Composable
fun ThemeIconButton(
    themeColorScheme: ColorScheme,
    modifier: Modifier = Modifier,
    surfaceColor: Color = themeColorScheme.surface,
    outlineColor: Color = surfaceColor,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    IconButton(
        modifier = modifier.size(themePreviewSize),
        enabled = enabled,
        onClick = onClick,
    ) {
        ThemePreviewIcon(
            surfaceColor = surfaceColor,
            outlineColor = outlineColor,
            tint = themeColorScheme.onPrimary.disabled(enabled.not()),
            background = themeColorScheme.primary.disabled(enabled.not()),
            iconShape = themePreviewShape,
            modifier = Modifier
                .padding(small_padding)
                .size(themePreviewSize)
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Preview(showBackground = true)
@Composable
private fun ThemePickerPreview() {
    ScrollableColumn {
        var selection by rememberSaveable { mutableStateOf(APPLICATION_THEME.default) }
        ThemePicker(
            selectedTheme = selection,
            title = R.string.select_theme,
            isDarkColors = isSystemInDarkTheme()
        ) {
            selection = it.name
        }
    }
}