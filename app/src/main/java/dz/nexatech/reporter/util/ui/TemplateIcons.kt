package dz.nexatech.reporter.util.ui

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.google.common.collect.ImmutableMap
import dz.nexatech.reporter.client.R
import dz.nexatech.reporter.client.common.putValue

const val TEMPLATES_ICONS_PATH = "icons/"
const val TEMPLATES_ICONS_EXTENSION = "svg"

class IconInfo(
    val name: String,
    @DrawableRes val drawable: Int,
) {
    val path = "$TEMPLATES_ICONS_PATH$name.$TEMPLATES_ICONS_EXTENSION"

    @Composable
    fun painterResource(): Painter = painterResource(drawable)

    override fun toString() = name

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as IconInfo
        if (name != other.name) return false
        return true
    }

    override fun hashCode() = name.hashCode()
}

val templateIcons = ImmutableMap.Builder<String, IconInfo>()
    .putValue(IconInfo("info", R.drawable.ms_info))
    .putValue(IconInfo("home", R.drawable.baseline_home_24))
    .build()

@Composable
fun painterResource(iconName: String) = templateIcons[iconName]!!.painterResource()