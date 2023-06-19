@file:OptIn(ExperimentalLayoutApi::class)

package dz.nexatech.reporter.util.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dz.nexatech.reporter.client.R
import dz.nexatech.reporter.util.model.rememberLayoutWidth

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

@Composable
fun ThemePicker(
    selection: String?,
    modifier: Modifier = Modifier,
    title: Int = R.string.select_theme,
    darkColors: Boolean = isSystemInDarkTheme(),
    onThemeSelected: (ThemeColors?) -> Unit,
) {
    val width by rememberLayoutWidth()
    val defaultColorScheme = Theme.colorScheme
    val surfaceColor = defaultColorScheme.surfaceVariant
    val selectedOutlineColor = defaultColorScheme.outline

    Card(modifier.width(width)) {
        PaddedColumn {
            Title(
                textRes = title,
                modifier = Modifier.contentPadding(),
                style = Theme.typography.titleLarge,
            )

            PaddedDivider()

            FlowRow(horizontalArrangement = Arrangement.Center) {
                for (theme in ThemeColors.values()) {
                    key(theme.name) {
                        val colorScheme = theme.colorScheme(darkColors)
                        IconButton(
                            modifier = Modifier.size(themePreviewSize),
                            onClick = { onThemeSelected.invoke(theme) },
                        ) {
                            ThemePreviewIcon(
                                surfaceColor = surfaceColor,
                                outlineColor = if (selection == theme.name) selectedOutlineColor else surfaceColor,
                                tint = colorScheme.onPrimaryContainer,
                                background = colorScheme.primaryContainer,
                                iconShape = themePreviewShape,
                                modifier = Modifier
                                    .padding(small_padding)
                                    .size(themePreviewSize),
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.padding(bottom = medium_padding, top = small_padding),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Body(R.string.default_theme, Modifier.padding(Theme.dimens.text_padding * 2))
                IconButton(
                    modifier = Modifier.size(themePreviewSize),
                    onClick = { onThemeSelected.invoke(null) },
                ) {
                    ThemePreviewIcon(
                        surfaceColor = surfaceColor,
                        outlineColor = if (selection == null) selectedOutlineColor else surfaceColor,
                        tint = defaultColorScheme.onPrimaryContainer,
                        background = defaultColorScheme.primaryContainer,
                        iconShape = themePreviewShape,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ThemePickerPreview() {
    var selection by remember { mutableStateOf<String?>(null) }
    ThemePicker(
        selection = selection,
        modifier = Modifier,
        title = R.string.select_theme,
        darkColors = isSystemInDarkTheme()
    ) {
        selection = it?.name
    }
}