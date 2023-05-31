package dz.nexatech.reporter.util.ui

import android.content.Context
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dz.nexatech.reporter.util.model.APPLICATION_THEME
import dz.nexatech.reporter.util.model.AppConfig
import dz.nexatech.reporter.util.model.DYNAMIC_TONAL_PALETTE_ENABLED
import dz.nexatech.reporter.util.model.LocalConfig
import dz.nexatech.reporter.util.model.Teller

object DefaultColors {
    val md_theme_light_error = Color(0xFFBA1A1A)
    val md_theme_light_errorContainer = Color(0xFFFFDAD6)
    val md_theme_light_onError = Color(0xFFFFFFFF)
    val md_theme_light_onErrorContainer = Color(0xFF410002)
    val md_theme_light_background = Color(0xFFE6E6E6)
    val md_theme_light_onBackground = Color(0xFF1A1C19)

    val md_theme_dark_error = Color(0xFFFFB4AB)
    val md_theme_dark_errorContainer = Color(0xFF93000A)
    val md_theme_dark_onError = Color(0xFF690005)
    val md_theme_dark_onErrorContainer = Color(0xFFFFDAD6)
    val md_theme_dark_background = Color(0xFF1B1B1F)
    val md_theme_dark_onBackground = Color(0xFFE5E1E6)
}

fun loadColorScheme(
    context: Context,
    useDynamicTonalPalette: Boolean,
    isDarkTheme: Boolean,
    themeNameConfig: LocalConfig<String>
): ColorScheme =
    if (useDynamicTonalPalette
        && AppConfig.get(DYNAMIC_TONAL_PALETTE_ENABLED)
        && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    ) {
        if (isDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    } else {
        val currentThemeName by AppConfig.stringState(themeNameConfig)
        val currentThemeColors = try {
            ThemeColors.valueOf(currentThemeName)
        } catch (e: IllegalArgumentException) {
            val defaultTheme = themeNameConfig.default
            Teller.warn("Theme not found: $currentThemeName falling back to: $defaultTheme", e)
            AppConfig.set(themeNameConfig, defaultTheme)
            ThemeColors.valueOf(defaultTheme)
        }
        currentThemeColors.colorScheme(isDarkTheme)
    }


@Composable
fun DynamicTheme(
    themeNameConfig: LocalConfig<String>,
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = loadColorScheme(LocalContext.current, false, isDarkTheme, themeNameConfig)
    MaterialTheme(
        colorScheme = colorScheme,
        content = content,
    )
}

@Composable
fun ApplicationTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    context: Context = LocalContext.current,
    content: @Composable () -> Unit,
) {
    val colorScheme = remember(context) {
        loadColorScheme(context, true, isDarkTheme, APPLICATION_THEME)
    }
    val systemUiController = rememberSystemUiController()

    DisposableEffect(systemUiController, isDarkTheme) {

        systemUiController.setSystemBarsColor(
            color = colorScheme.surface,
            darkIcons = !isDarkTheme,
        )

        onDispose {}
    }

    val materialTypography = MaterialTheme.typography
    val typography = remember(materialTypography) {
        materialTypography.copy(
            titleMedium = materialTypography.titleMedium.copy(
                fontSize = 19.sp,
                lineHeight = 26.sp,
            ),
            titleSmall = materialTypography.titleMedium
        )
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = content,
    )
}

@Composable
fun InfoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ThemeColors.GREEN.colorScheme(darkTheme),
        content = content,
    )
}

@Composable
fun WarningTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable() () -> Unit
) {
    MaterialTheme(
        colorScheme = ThemeColors.YELLOW.colorScheme(darkTheme),
        content = content,
    )
}

@Composable
fun ErrorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable() () -> Unit
) {
    MaterialTheme(
        colorScheme = ThemeColors.RED.colorScheme(darkTheme),
        content = content,
    )
}