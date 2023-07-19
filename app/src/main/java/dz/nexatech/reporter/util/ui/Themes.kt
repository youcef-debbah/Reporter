package dz.nexatech.reporter.util.ui

import android.content.Context
import android.os.Build
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dz.nexatech.reporter.util.model.APPLICATION_THEME
import dz.nexatech.reporter.util.model.AppConfig
import dz.nexatech.reporter.util.model.DYNAMIC_APPLICATION_THEME
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
    themeName: String,
): ColorScheme? =
    if (useDynamicTonalPalette && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (isDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    } else {
        try {
            ThemeColors.valueOf(themeName).colorScheme(isDarkTheme)
        } catch (e: IllegalArgumentException) {
            Teller.warn("Theme not found: $themeName", e)
            null
        }
    }

@Composable
fun DynamicTheme(
    themeNameState: State<String>,
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val colorSchemeState = remember(context, isDarkTheme) {
        derivedStateOf {
            loadColorScheme(context, false, isDarkTheme, themeNameState.value)
        }
    }

    val colorScheme = colorSchemeState.value
    if (colorScheme != null) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content,
        )
    }
}

@Composable
fun DynamicTheme(
    themeName: String,
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val colorScheme = remember(context, themeName, isDarkTheme) {
        loadColorScheme(context, false, isDarkTheme, themeName)
    }

    if (colorScheme != null) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content,
        )
    }
}

@Composable
fun AnimatedApplicationTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    context: Context = LocalContext.current,
    content: @Composable () -> Unit,
) {
    val colorScheme by remember(isDarkTheme, context) {
        derivedStateOf {
            val appThemeName = AppConfig.getState(APPLICATION_THEME)
            val useDynamicTheme = AppConfig.getState(DYNAMIC_APPLICATION_THEME)
            loadColorScheme(context, useDynamicTheme.value, isDarkTheme, appThemeName.value)
                ?: ThemeColors.DEFAULT_THEME.colorScheme(isDarkTheme).also { appThemeName.reset() }
        }
    }

    Crossfade(colorScheme, label = "theme_animation") {
        SetSystemBarsColor(isDarkTheme, it)
        MaterialTheme(
            colorScheme = it,
            typography = rememberTypography(),
            content = content,
        )
    }
}

@Composable
fun DynamicApplicationTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    context: Context = LocalContext.current,
    content: @Composable () -> Unit,
) {
    val colorScheme by remember(isDarkTheme, context) {
        derivedStateOf {
            val appThemeName = AppConfig.getState(APPLICATION_THEME)
            val useDynamicTheme = AppConfig.getState(DYNAMIC_APPLICATION_THEME)
            loadColorScheme(context, useDynamicTheme.value, isDarkTheme, appThemeName.value)
                ?: ThemeColors.DEFAULT_THEME.colorScheme(isDarkTheme).also { appThemeName.reset() }
        }
    }

    SetSystemBarsColor(isDarkTheme, colorScheme)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = rememberTypography(),
        content = content,
    )
}

@Composable
fun StaticApplicationTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    context: Context = LocalContext.current,
    content: @Composable () -> Unit,
) {
    val colorScheme = remember(isDarkTheme, context) {
        val appThemeName = AppConfig.get(APPLICATION_THEME)
        val useDynamicTheme = AppConfig.get(DYNAMIC_APPLICATION_THEME)
        loadColorScheme(context, useDynamicTheme, isDarkTheme, appThemeName)
            ?: ThemeColors.DEFAULT_THEME.colorScheme(isDarkTheme)
    }

    SetSystemBarsColor(isDarkTheme, colorScheme)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = rememberTypography(),
        content = content,
    )
}

@Composable
private fun rememberTypography(): Typography {
    val materialTypography = MaterialTheme.typography
    val typography = remember(materialTypography) {
        materialTypography.copy(
            titleMedium = materialTypography.titleMedium.copy(
                fontSize = 19.sp,
                lineHeight = 24.sp,
            ),
            titleSmall = materialTypography.titleSmall.copy(
                fontSize = 17.sp,
                lineHeight = 24.sp,
            ),
        )
    }
    return typography
}

@Composable
fun SetSystemBarsColor(
    isDarkTheme: Boolean,
    colorScheme: ColorScheme,
) {
    val systemUiController = rememberSystemUiController()
    DisposableEffect(systemUiController, isDarkTheme) {

        systemUiController.setSystemBarsColor(
            color = colorScheme.surface,
            darkIcons = !isDarkTheme,
        )

        onDispose {}
    }
}

@Composable
fun InfoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = ThemeColors.GREEN.colorScheme(darkTheme),
        content = content,
    )
}

@Composable
fun WarningTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable() () -> Unit,
) {
    MaterialTheme(
        colorScheme = ThemeColors.YELLOW.colorScheme(darkTheme),
        content = content,
    )
}

@Composable
fun ErrorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable() () -> Unit,
) {
    MaterialTheme(
        colorScheme = ThemeColors.RED.colorScheme(darkTheme),
        content = content,
    )
}