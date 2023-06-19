@file:OptIn(ExperimentalLayoutApi::class)

package dz.nexatech.reporter.util.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
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
    isDarkColors: Boolean = isSystemInDarkTheme(),
    onThemeSelected: (ThemeColors?) -> Unit,
) {
    val width by rememberLayoutWidth()
    val defaultColorScheme = Theme.colorScheme

    val context = LocalContext.current
    val selectionColorScheme =
        if (selection != null) {
            loadColorScheme(context, false, isDarkColors, selection)
        } else {
            Theme.colorScheme
        }

    MaterialTheme(selectionColorScheme) {
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
                            IconButton(
                                modifier = Modifier.size(themePreviewSize),
                                onClick = { onThemeSelected.invoke(theme) },
                            ) {
                                val colorScheme = theme.colorScheme(isDarkColors)
                                ThemePreviewIcon(
                                    surfaceColor = selectionColorScheme.surfaceVariant,
                                    outlineColor = if (selection == theme.name) selectionColorScheme.outline else selectionColorScheme.surfaceVariant,
                                    tint = colorScheme.onPrimary,
                                    background = colorScheme.primary,
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
                    Body(
                        R.string.default_theme,
                        Modifier.padding(Theme.dimens.text_padding * 2)
                    )
                    IconButton(
                        modifier = Modifier.size(themePreviewSize),
                        onClick = { onThemeSelected.invoke(null) },
                    ) {
                        ThemePreviewIcon(
                            surfaceColor = selectionColorScheme.surfaceVariant,
                            outlineColor = if (selection == null) selectionColorScheme.outline else selectionColorScheme.surfaceVariant,
                            tint = defaultColorScheme.onPrimary,
                            background = defaultColorScheme.primary,
                            iconShape = themePreviewShape,
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ThemePickerPreview() {
    ScrollableColumn {
        var selection by rememberSaveable { mutableStateOf<String?>(null) }
        ThemePicker(
            selection = selection,
            modifier = Modifier,
            title = R.string.select_theme,
            isDarkColors = isSystemInDarkTheme()
        ) {
            selection = it?.name
        }
    }
}