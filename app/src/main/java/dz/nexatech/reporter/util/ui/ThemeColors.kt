package dz.nexatech.reporter.util.ui

import androidx.annotation.StringRes
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import dz.nexatech.reporter.client.R
import dz.nexatech.reporter.client.common.atomicLazy

@Stable
enum class ThemeColors(
    @StringRes val title: Int,
    val seed: Color,
    lightMain: Color,
    lightOnMain: Color,
    lightContainer: Color,
    lightOnContainer: Color,
    lightSurface: Color,
    lightOnSurface: Color,
    lightSurfaceVariant: Color,
    lightOnSurfaceVariant: Color,

    lightOutline: Color,
    lightInverseOnSurface: Color,
    lightInverseSurface: Color,
    lightInverseMain: Color,
    lightSurfaceTint: Color,
    lightOutlineVariant: Color,
    lightScrim: Color,

    darkMain: Color,
    darkOnMain: Color,
    darkContainer: Color,
    darkOnContainer: Color,
    darkSurface: Color,
    darkOnSurface: Color,
    darkSurfaceVariant: Color,
    darkOnSurfaceVariant: Color,

    darkOutline: Color,
    darkInverseOnSurface: Color,
    darkInverseSurface: Color,
    darkInverseMain: Color,
    darkSurfaceTint: Color,
    darkOutlineVariant: Color,
    darkScrim: Color,

    lightBackground: Color = lightSurface,
    lightOnBackground: Color = lightOnSurface,

    lightError: Color = DefaultColors.md_theme_light_error,
    lightErrorContainer: Color = DefaultColors.md_theme_light_errorContainer,
    lightOnError: Color = DefaultColors.md_theme_light_onError,
    lightOnErrorContainer: Color = DefaultColors.md_theme_light_onErrorContainer,

    darkBackground: Color = darkSurface,
    darkOnBackground: Color = darkOnSurface,

    darkError: Color = DefaultColors.md_theme_dark_error,
    darkErrorContainer: Color = DefaultColors.md_theme_dark_errorContainer,
    darkOnError: Color = DefaultColors.md_theme_dark_onError,
    darkOnErrorContainer: Color = DefaultColors.md_theme_dark_onErrorContainer,
) {

    VIOLET(
        title = R.string.violet_theme_title,
        seed = Color(0xFF8000FF),
        lightMain = Color(0xFF7D00FA),
        lightOnMain = Color(0xFFFFFFFF),
        lightContainer = Color(0xFFECDCFF),
        lightOnContainer = Color(0xFF270057),
        lightSurface = Color(0xFFFFFBFF),
        lightOnSurface = Color(0xFF1D1B1E),
        lightSurfaceVariant = Color(0xFFE8E0EB),
        lightOnSurfaceVariant = Color(0xFF49454E),
        lightOutline = Color(0xFF7B757F),
        lightInverseOnSurface = Color(0xFFF5EFF4),
        lightInverseSurface = Color(0xFF323033),
        lightInverseMain = Color(0xFFD5BAFF),
        lightSurfaceTint = Color(0xFF7D00FA),
        lightOutlineVariant = Color(0xFFCBC4CF),
        lightScrim = Color(0xFF000000),
        darkMain = Color(0xFFD5BAFF),
        darkOnMain = Color(0xFF42008A),
        darkContainer = Color(0xFF5F00C0),
        darkOnContainer = Color(0xFFECDCFF),
        darkSurface = Color(0xFF1D1B1E),
        darkOnSurface = Color(0xFFE6E1E6),
        darkSurfaceVariant = Color(0xFF49454E),
        darkOnSurfaceVariant = Color(0xFFCBC4CF),
        darkOutline = Color(0xFF958E99),
        darkInverseOnSurface = Color(0xFF1D1B1E),
        darkInverseSurface = Color(0xFFE6E1E6),
        darkInverseMain = Color(0xFF7D00FA),
        darkSurfaceTint = Color(0xFFD5BAFF),
        darkOutlineVariant = Color(0xFF49454E),
        darkScrim = Color(0xFF000000),
    ),

    BLUE(
        title = R.string.blue_theme_title,
        seed = Color(0xFF616AE3),
        lightMain = Color(0xFF4750C8),
        lightOnMain = Color(0xFFFFFFFF),
        lightContainer = Color(0xFFC8C8FF),
        lightOnContainer = Color(0xFF00016D),
        lightSurface = Color(0xFFFFFBFF),
        lightOnSurface = Color(0xFF1B1B1F),
        lightSurfaceVariant = Color(0xFFE3E1EC),
        lightOnSurfaceVariant = Color(0xFF46464F),
        lightOutline = Color(0xFF777680),
        lightInverseOnSurface = Color(0xFFF3EFF4),
        lightInverseSurface = Color(0xFF303034),
        lightInverseMain = Color(0xFFBEC2FF),
        lightSurfaceTint = Color(0xFF4750C8),
        lightOutlineVariant = Color(0xFFC7C5D0),
        lightScrim = Color(0xFF000000),
        darkMain = Color(0xFFBEC2FF),
        darkOnMain = Color(0xFF0F159B),
        darkContainer = Color(0xFF2D35B0),
        darkOnContainer = Color(0xFFE0E0FF),
        darkSurface = Color(0xFF1B1B1F),
        darkOnSurface = Color(0xFFE5E1E6),
        darkSurfaceVariant = Color(0xFF46464F),
        darkOnSurfaceVariant = Color(0xFFC7C5D0),
        darkOutline = Color(0xFF91909A),
        darkInverseOnSurface = Color(0xFF1B1B1F),
        darkInverseSurface = Color(0xFFE5E1E6),
        darkInverseMain = Color(0xFF4750C8),
        darkSurfaceTint = Color(0xFFBEC2FF),
        darkOutlineVariant = Color(0xFF46464F),
        darkScrim = Color(0xFF000000),
    ),

    AZURE(
        title = R.string.azure_theme_title,
        seed = Color(0xFF0080FF),
        lightMain = Color(0xFF005CBB),
        lightOnMain = Color(0xFFFFFFFF),
        lightContainer = Color(0xFFD7E3FF),
        lightOnContainer = Color(0xFF001B3F),
        lightSurface = Color(0xFFFDFBFF),
        lightOnSurface = Color(0xFF1A1B1F),
        lightSurfaceVariant = Color(0xFFE0E2EC),
        lightOnSurfaceVariant = Color(0xFF44474E),
        lightOutline = Color(0xFF74777F),
        lightInverseOnSurface = Color(0xFFF2F0F4),
        lightInverseSurface = Color(0xFF2F3033),
        lightInverseMain = Color(0xFFABC7FF),
        lightSurfaceTint = Color(0xFF005CBB),
        lightOutlineVariant = Color(0xFFC4C6D0),
        lightScrim = Color(0xFF000000),
        darkMain = Color(0xFFABC7FF),
        darkOnMain = Color(0xFF002F65),
        darkContainer = Color(0xFF00458F),
        darkOnContainer = Color(0xFFD7E3FF),
        darkSurface = Color(0xFF1A1B1F),
        darkOnSurface = Color(0xFFE3E2E6),
        darkSurfaceVariant = Color(0xFF44474E),
        darkOnSurfaceVariant = Color(0xFFC4C6D0),
        darkOutline = Color(0xFF8E9099),
        darkInverseOnSurface = Color(0xFF1A1B1F),
        darkInverseSurface = Color(0xFFE3E2E6),
        darkInverseMain = Color(0xFF005CBB),
        darkSurfaceTint = Color(0xFFABC7FF),
        darkOutlineVariant = Color(0xFF44474E),
        darkScrim = Color(0xFF000000),
    ),

    CYAN(
        title = R.string.cyan_theme_title,
        seed = Color(0xFF68FBFA),
        lightMain = Color(0xFF006A69),
        lightOnMain = Color(0xFFFFFFFF),
        lightContainer = Color(0xFF64F8F7),
        lightOnContainer = Color(0xFF002020),
        lightSurface = Color(0xFFFAFDFC),
        lightOnSurface = Color(0xFF191C1C),
        lightSurfaceVariant = Color(0xFFDAE5E4),
        lightOnSurfaceVariant = Color(0xFF3F4948),
        lightOutline = Color(0xFF6F7978),
        lightInverseOnSurface = Color(0xFFEFF1F0),
        lightInverseSurface = Color(0xFF2D3131),
        lightInverseMain = Color(0xFF3EDBDA),
        lightSurfaceTint = Color(0xFF006A69),
        lightOutlineVariant = Color(0xFFBEC9C8),
        lightScrim = Color(0xFF000000),
        darkMain = Color(0xFF3EDBDA),
        darkOnMain = Color(0xFF003737),
        darkContainer = Color(0xFF00504F),
        darkOnContainer = Color(0xFF64F8F7),
        darkSurface = Color(0xFF191C1C),
        darkOnSurface = Color(0xFFE0E3E2),
        darkSurfaceVariant = Color(0xFF3F4948),
        darkOnSurfaceVariant = Color(0xFFBEC9C8),
        darkOutline = Color(0xFF889392),
        darkInverseOnSurface = Color(0xFF191C1C),
        darkInverseSurface = Color(0xFFE0E3E2),
        darkInverseMain = Color(0xFF006A69),
        darkSurfaceTint = Color(0xFF3EDBDA),
        darkOutlineVariant = Color(0xFF3F4948),
        darkScrim = Color(0xFF000000),
    ),

//    SPRING_GREEN(
//        title = R.string.spring_green_theme_title,
//        seed = Color(0xFF00FF80),
//        lightMain = Color(0xFF006D33),
//        lightOnMain = Color(0xFFFFFFFF),
//        lightContainer = Color(0xFF63FF94),
//        lightOnContainer = Color(0xFF00210B),
//        lightSurface = Color(0xFFFCFDF7),
//        lightOnSurface = Color(0xFF191C19),
//        lightSurfaceVariant = Color(0xFFDDE5DA),
//        lightOnSurfaceVariant = Color(0xFF414941),
//        lightOutline = Color(0xFF717970),
//        lightInverseOnSurface = Color(0xFFF0F1EC),
//        lightInverseSurface = Color(0xFF2E312E),
//        lightInverseMain = Color(0xFF00E472),
//        lightSurfaceTint = Color(0xFF006D33),
//        lightOutlineVariant = Color(0xFFC1C9BE),
//        lightScrim = Color(0xFF000000),
//        darkMain = Color(0xFF00E472),
//        darkOnMain = Color(0xFF003917),
//        darkContainer = Color(0xFF005225),
//        darkOnContainer = Color(0xFF63FF94),
//        darkSurface = Color(0xFF191C19),
//        darkOnSurface = Color(0xFFE2E3DE),
//        darkSurfaceVariant = Color(0xFF414941),
//        darkOnSurfaceVariant = Color(0xFFC1C9BE),
//        darkOutline = Color(0xFF8B9389),
//        darkInverseOnSurface = Color(0xFF191C19),
//        darkInverseSurface = Color(0xFFE2E3DE),
//        darkInverseMain = Color(0xFF006D33),
//        darkSurfaceTint = Color(0xFF00E472),
//        darkOutlineVariant = Color(0xFF414941),
//        darkScrim = Color(0xFF000000),
//    ),

    GREEN(
        title = R.string.green_theme_title,
        seed = Color(0xFF003913),
        lightMain = Color(0xFF1F7938),
        lightOnMain = Color(0xFFFFFFFF),
        lightContainer = Color(0xFFA3F5AA),
        lightOnContainer = Color(0xFF002108),
        lightSurface = Color(0xFFE8F5E9),
        lightOnSurface = Color(0xFF1A1C19),
        lightSurfaceVariant = Color(0xFFC8E6C9),
        lightOnSurfaceVariant = Color(0xFF424940),
        lightOutline = Color(0xFF727970),
        lightInverseOnSurface = Color(0xFFF0F1EB),
        lightInverseSurface = Color(0xFF2E312D),
        lightInverseMain = Color(0xFF88D990),
        lightSurfaceTint = Color(0xFF1F7938),
        lightOutlineVariant = Color(0xFFC1C9BE),
        lightScrim = Color(0xFF000000),
        darkMain = Color(0xFF88D990),
        darkOnMain = Color(0xFF003913),
        darkContainer = Color(0xFF00531F),
        darkOnContainer = Color(0xFFA3F5AA),
        darkSurface = Color(0xFF1A1C19),
        darkOnSurface = Color(0xFFE2E3DD),
        darkSurfaceVariant = Color(0xFF424940),
        darkOnSurfaceVariant = Color(0xFFC1C9BE),
        darkOutline = Color(0xFF8B9389),
        darkInverseOnSurface = Color(0xFF1A1C19),
        darkInverseSurface = Color(0xFFE2E3DD),
        darkInverseMain = Color(0xFF1A6C31),
        darkSurfaceTint = Color(0xFF88D990),
        darkOutlineVariant = Color(0xFF424940),
        darkScrim = Color(0xFF000000),
    ),

    CHARTREUSE_GREEN(
        title = R.string.chartreuse_green_theme_title,
        seed = Color(0xFF80FF00),
        lightMain = Color(0xFF326B00),
        lightOnMain = Color(0xFFFFFFFF),
        lightContainer = Color(0xFF82FF10),
        lightOnContainer = Color(0xFF0B2000),
        lightSurface = Color(0xFFFDFDF5),
        lightOnSurface = Color(0xFF1A1C18),
        lightSurfaceVariant = Color(0xFFE0E4D6),
        lightOnSurfaceVariant = Color(0xFF44483E),
        lightOutline = Color(0xFF74796D),
        lightInverseOnSurface = Color(0xFFF1F1EA),
        lightInverseSurface = Color(0xFF2F312C),
        lightInverseMain = Color(0xFF70E000),
        lightSurfaceTint = Color(0xFF326B00),
        lightOutlineVariant = Color(0xFFC4C8BB),
        lightScrim = Color(0xFF000000),
        darkMain = Color(0xFF70E000),
        darkOnMain = Color(0xFF173800),
        darkContainer = Color(0xFF245100),
        darkOnContainer = Color(0xFF82FF10),
        darkSurface = Color(0xFF1A1C18),
        darkOnSurface = Color(0xFFE3E3DC),
        darkSurfaceVariant = Color(0xFF44483E),
        darkOnSurfaceVariant = Color(0xFFC4C8BB),
        darkOutline = Color(0xFF8E9286),
        darkInverseOnSurface = Color(0xFF1A1C18),
        darkInverseSurface = Color(0xFFE3E3DC),
        darkInverseMain = Color(0xFF326B00),
        darkSurfaceTint = Color(0xFF70E000),
        darkOutlineVariant = Color(0xFF44483E),
        darkScrim = Color(0xFF000000),
    ),

    YELLOW(
        title = R.string.yellow_theme_title,
        seed = Color(0xFFFFEB3B),
        lightMain = Color(0xFF695F00),
        lightOnMain = Color(0xFFFFFFFF),
        lightContainer = Color(0xFFF9E534),
        lightOnContainer = Color(0xFF201C00),
        lightSurface = Color(0xFFFFFBFF),
        lightOnSurface = Color(0xFF1D1C16),
        lightSurfaceVariant = Color(0xFFE8E2D0),
        lightOnSurfaceVariant = Color(0xFF4A473A),
        lightOutline = Color(0xFF7B7768),
        lightInverseOnSurface = Color(0xFFF5F0E7),
        lightInverseSurface = Color(0xFF32302A),
        lightInverseMain = Color(0xFFDBC90A),
        lightSurfaceTint = Color(0xFF695F00),
        lightOutlineVariant = Color(0xFFCBC6B5),
        lightScrim = Color(0xFF000000),
        darkMain = Color(0xFFDBC90A),
        darkOnMain = Color(0xFF363100),
        darkContainer = Color(0xFF4F4800),
        darkOnContainer = Color(0xFFF9E534),
        darkSurface = Color(0xFF1D1C16),
        darkOnSurface = Color(0xFFE7E2D9),
        darkSurfaceVariant = Color(0xFF4A473A),
        darkOnSurfaceVariant = Color(0xFFCBC6B5),
        darkOutline = Color(0xFF959181),
        darkInverseOnSurface = Color(0xFF1D1C16),
        darkInverseSurface = Color(0xFFE7E2D9),
        darkInverseMain = Color(0xFF695F00),
        darkSurfaceTint = Color(0xFFDBC90A),
        darkOutlineVariant = Color(0xFF4A473A),
        darkScrim = Color(0xFF000000),
    ),

    ORANGE(
        title = R.string.orange_theme_title,
        seed = Color(0xFFFF8000),
        lightMain = Color(0xFF964900),
        lightOnMain = Color(0xFFFFFFFF),
        lightContainer = Color(0xFFFFDCC7),
        lightOnContainer = Color(0xFF311300),
        lightSurface = Color(0xFFFFFBFF),
        lightOnSurface = Color(0xFF201A17),
        lightSurfaceVariant = Color(0xFFF4DED3),
        lightOnSurfaceVariant = Color(0xFF52443C),
        lightOutline = Color(0xFF84746A),
        lightInverseOnSurface = Color(0xFFFBEEE8),
        lightInverseSurface = Color(0xFF362F2B),
        lightInverseMain = Color(0xFFFFB787),
        lightSurfaceTint = Color(0xFF964900),
        lightOutlineVariant = Color(0xFFD7C3B8),
        lightScrim = Color(0xFF000000),
        darkMain = Color(0xFFFFB787),
        darkOnMain = Color(0xFF502400),
        darkContainer = Color(0xFF723600),
        darkOnContainer = Color(0xFFFFDCC7),
        darkSurface = Color(0xFF201A17),
        darkOnSurface = Color(0xFFECE0DA),
        darkSurfaceVariant = Color(0xFF52443C),
        darkOnSurfaceVariant = Color(0xFFD7C3B8),
        darkOutline = Color(0xFF9F8D83),
        darkInverseOnSurface = Color(0xFF201A17),
        darkInverseSurface = Color(0xFFECE0DA),
        darkInverseMain = Color(0xFF964900),
        darkSurfaceTint = Color(0xFFFFB787),
        darkOutlineVariant = Color(0xFF52443C),
        darkScrim = Color(0xFF000000),
    ),

    RED(
        title = R.string.red_theme_title,
        seed = Color(0xFFFF5540),
        lightMain = Color(0xFFD32F2F),
        lightOnMain = Color(0xFFFFFFFF),
        lightContainer = Color(0xFFFFAEAE),
        lightOnContainer = Color(0xFF410000),
        lightSurface = Color(0xFFFFEBEE),
        lightOnSurface = Color(0xFF201A19),
        lightSurfaceVariant = Color(0xFFFFCDD2),
        lightOnSurfaceVariant = Color(0xFF534341),
        lightOutline = Color(0xFF857370),
        lightInverseOnSurface = Color(0xFFFBEEEC),
        lightInverseSurface = Color(0xFF362F2E),
        lightInverseMain = Color(0xFFFFB4A8),
        lightSurfaceTint = Color(0xFFB72114),
        lightOutlineVariant = Color(0xFFD8C2BE),
        lightScrim = Color(0xFF000000),
        darkMain = Color(0xFFFFB4A8),
        darkOnMain = Color(0xFF680100),
        darkContainer = Color(0xFF930200),
        darkOnContainer = Color(0xFFFFDAD4),
        darkSurface = Color(0xFF201A19),
        darkOnSurface = Color(0xFFEDE0DD),
        darkSurfaceVariant = Color(0xFF534341),
        darkOnSurfaceVariant = Color(0xFFD8C2BE),
        darkOutline = Color(0xFFA08C89),
        darkInverseOnSurface = Color(0xFF201A19),
        darkInverseSurface = Color(0xFFEDE0DD),
        darkInverseMain = Color(0xFFB72114),
        darkSurfaceTint = Color(0xFFFFB4A8),
        darkOutlineVariant = Color(0xFF534341),
        darkScrim = Color(0xFF000000),
    ),

//    ROSE(
//        title = R.string.rose_theme_title,
//        seed = Color(0xFFFF0080),
//        lightMain = Color(0xFFBA005C),
//        lightOnMain = Color(0xFFFFFFFF),
//        lightContainer = Color(0xFFFFD9E1),
//        lightOnContainer = Color(0xFF3F001B),
//        lightSurface = Color(0xFFFFFBFF),
//        lightOnSurface = Color(0xFF201A1B),
//        lightSurfaceVariant = Color(0xFFF3DDE1),
//        lightOnSurfaceVariant = Color(0xFF514346),
//        lightOutline = Color(0xFF847376),
//        lightInverseOnSurface = Color(0xFFFAEEEF),
//        lightInverseSurface = Color(0xFF352F30),
//        lightInverseMain = Color(0xFFFFB1C5),
//        lightSurfaceTint = Color(0xFFBA005C),
//        lightOutlineVariant = Color(0xFFD6C2C5),
//        lightScrim = Color(0xFF000000),
//        darkMain = Color(0xFFFFB1C5),
//        darkOnMain = Color(0xFF65002F),
//        darkContainer = Color(0xFF8F0045),
//        darkOnContainer = Color(0xFFFFD9E1),
//        darkSurface = Color(0xFF201A1B),
//        darkOnSurface = Color(0xFFECE0E1),
//        darkSurfaceVariant = Color(0xFF514346),
//        darkOnSurfaceVariant = Color(0xFFD6C2C5),
//        darkOutline = Color(0xFF9E8C90),
//        darkInverseOnSurface = Color(0xFF201A1B),
//        darkInverseSurface = Color(0xFFECE0E1),
//        darkInverseMain = Color(0xFFBA005C),
//        darkSurfaceTint = Color(0xFFFFB1C5),
//        darkOutlineVariant = Color(0xFF514346),
//        darkScrim = Color(0xFF000000),
//    ),

    MAGENTA(
        title = R.string.magenta_theme_title,
        seed = Color(0xFFFF00FF),
        lightMain = Color(0xFFA900A9),
        lightOnMain = Color(0xFFFFFFFF),
        lightContainer = Color(0xFFFFD7F5),
        lightOnContainer = Color(0xFF380038),
        lightSurface = Color(0xFFFFFBFF),
        lightOnSurface = Color(0xFF1E1A1D),
        lightSurfaceVariant = Color(0xFFEEDEE7),
        lightOnSurfaceVariant = Color(0xFF4E444B),
        lightOutline = Color(0xFF80747C),
        lightInverseOnSurface = Color(0xFFF8EEF2),
        lightInverseSurface = Color(0xFF342F32),
        lightInverseMain = Color(0xFFFFABF3),
        lightSurfaceTint = Color(0xFFA900A9),
        lightOutlineVariant = Color(0xFFD1C2CB),
        lightScrim = Color(0xFF000000),
        darkMain = Color(0xFFFFABF3),
        darkOnMain = Color(0xFF5B005B),
        darkContainer = Color(0xFF810081),
        darkOnContainer = Color(0xFFFFD7F5),
        darkSurface = Color(0xFF1E1A1D),
        darkOnSurface = Color(0xFFE9E0E4),
        darkSurfaceVariant = Color(0xFF4E444B),
        darkOnSurfaceVariant = Color(0xFFD1C2CB),
        darkOutline = Color(0xFF9A8D95),
        darkInverseOnSurface = Color(0xFF1E1A1D),
        darkInverseSurface = Color(0xFFE9E0E4),
        darkInverseMain = Color(0xFFA900A9),
        darkSurfaceTint = Color(0xFFFFABF3),
        darkOutlineVariant = Color(0xFF4E444B),
        darkScrim = Color(0xFF000000),
    ),

    ;

    val lightColors by atomicLazy {
        lightColorScheme(
            primary = lightMain,
            onPrimary = lightOnMain,
            primaryContainer = lightContainer,
            onPrimaryContainer = lightOnContainer,
            secondary = lightMain,
            onSecondary = lightOnMain,
            secondaryContainer = lightContainer,
            onSecondaryContainer = lightOnContainer,
            tertiary = lightMain,
            onTertiary = lightOnMain,
            tertiaryContainer = lightContainer,
            onTertiaryContainer = lightOnContainer,
            error = lightError,
            errorContainer = lightErrorContainer,
            onError = lightOnError,
            onErrorContainer = lightOnErrorContainer,
            background = lightBackground,
            onBackground = lightOnBackground,
            surface = lightSurface,
            onSurface = lightOnSurface,
            surfaceVariant = lightSurfaceVariant,
            onSurfaceVariant = lightOnSurfaceVariant,
            outline = lightOutline,
            inverseOnSurface = lightInverseOnSurface,
            inverseSurface = lightInverseSurface,
            inversePrimary = lightInverseMain,
            surfaceTint = lightSurfaceTint,
            outlineVariant = lightOutlineVariant,
            scrim = lightScrim,
        )
    }

    val darkColors by atomicLazy {
        darkColorScheme(
            primary = darkMain,
            onPrimary = darkOnMain,
            primaryContainer = darkContainer,
            onPrimaryContainer = darkOnContainer,
            secondary = darkMain,
            onSecondary = darkOnMain,
            secondaryContainer = darkContainer,
            onSecondaryContainer = darkOnContainer,
            tertiary = darkMain,
            onTertiary = darkOnMain,
            tertiaryContainer = darkContainer,
            onTertiaryContainer = darkOnContainer,
            error = darkError,
            errorContainer = darkErrorContainer,
            onError = darkOnError,
            onErrorContainer = darkOnErrorContainer,
            background = darkBackground,
            onBackground = darkOnBackground,
            surface = darkSurface,
            onSurface = darkOnSurface,
            surfaceVariant = darkSurfaceVariant,
            onSurfaceVariant = darkOnSurfaceVariant,
            outline = darkOutline,
            inverseOnSurface = darkInverseOnSurface,
            inverseSurface = darkInverseSurface,
            inversePrimary = darkInverseMain,
            surfaceTint = darkSurfaceTint,
            outlineVariant = darkOutlineVariant,
            scrim = darkScrim,
        )
    }

    fun colorScheme(darkTheme: Boolean): ColorScheme = if (darkTheme) darkColors else lightColors

    companion object {
        val DEFAULT_THEME: ThemeColors = BLUE
    }
}