package com.reporter.util.ui

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.reporter.util.model.AppConfig
import com.reporter.util.model.LOCAL_APPLICATION_THEME
import com.reporter.util.model.REMOTE_DYNAMIC_COLOR_SCHEME_ENABLED

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

@Composable
fun DynamicTheme(
    themeName: String = "",
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    DynamicTheme(
        themeColors = themeColorsNamed(themeName),
        darkTheme = darkTheme,
        content = content,
    )
}

@Composable
fun DynamicTheme(
    themeColors: ThemeColors = defaultThemeColors,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = themeColors.colorScheme(darkTheme),
        content = content,
    )
}

@Composable
fun DefaultTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable() () -> Unit
) {
    val currentTheme by AppConfig.stringState(LOCAL_APPLICATION_THEME)

    val colorScheme =
        if (AppConfig.get(REMOTE_DYNAMIC_COLOR_SCHEME_ENABLED) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        } else {
            defaultThemeColors.colorScheme(darkTheme)
        }

    val systemUiController = rememberSystemUiController()

    DisposableEffect(systemUiController, darkTheme) {

        systemUiController.setSystemBarsColor(
            color = colorScheme.surface,
            darkIcons = !darkTheme,
        )

        onDispose {}
    }

    MaterialTheme(
        colorScheme = colorScheme,
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