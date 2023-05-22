package dz.nexatech.reporter.util.ui

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.google.common.collect.ImmutableMap
import dz.nexatech.reporter.client.R
import dz.nexatech.reporter.client.common.FilesExtension
import dz.nexatech.reporter.client.common.putValue
import dz.nexatech.reporter.client.model.AssetResource
import dz.nexatech.reporter.client.model.ResourcesRepository

const val ICON_RESOURCE_PREFIX = "icons/"
const val ICON_RESOURCE_EXTENSION = FilesExtension.SVG

abstract class AbstractIconResource(
    val name: String,
) : AssetResource(
    "$ICON_RESOURCE_PREFIX$name.$ICON_RESOURCE_EXTENSION",
    ICON_RESOURCE_EXTENSION
) {
    @Composable
    abstract fun painterResource(resourcesRepository: ResourcesRepository): Painter

    final override fun toString() = path
}

private class DrawableIconResource(
    name: String,
    @DrawableRes val drawable: Int
) : AbstractIconResource(name) {
    @Composable
    override fun painterResource(resourcesRepository: ResourcesRepository): Painter =
        painterResource(drawable)
}

val iconsAssetsResources = ImmutableMap.Builder<String, AbstractIconResource>()
    .putValue(DrawableIconResource("info", R.drawable.ms_info))
    .putValue(DrawableIconResource("home", R.drawable.baseline_home_24))
    .build()