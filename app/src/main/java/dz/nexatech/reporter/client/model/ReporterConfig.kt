package dz.nexatech.reporter.client.model

import com.google.common.collect.ImmutableMap
import com.itextpdf.kernel.pdf.CompressionConstants
import dz.nexatech.reporter.util.model.LocalConfig
import dz.nexatech.reporter.util.model.RemoteConfig
import dz.nexatech.reporter.util.model.putLocalConfig
import dz.nexatech.reporter.util.model.putRemoteConfigDefault

val TEMPLATES_LIST_LOADING_ANIMATION_ENABLED =
    RemoteConfig.Boolean("TEMPLATES_LIST_LOADING_ANIMATION_ENABLED", true)
val PDF_RESOURCES_CACHING_ENABLED = RemoteConfig.Boolean("PDF_RESOURCES_CACHING_ENABLED", false)
val PDF_COMPRESSION_LEVEL =
    RemoteConfig.Int("PDF_COMPRESSION_LEVEL", CompressionConstants.BEST_COMPRESSION)
val COLOR_PICKER_SIZE = RemoteConfig.Int("COLOR_PICKER_SIZE", 220)
val OUTLINED_FIELD_DROP_MENU_OFFSET = RemoteConfig.Int("OUTLINED_FIELD_DROP_MENU_OFFSET", 0)

val REPORTER_REMOTE_CONFIGS_DEFAULTS: ImmutableMap<String, Any> = ImmutableMap.builder<String, Any>()
    .putRemoteConfigDefault(TEMPLATES_LIST_LOADING_ANIMATION_ENABLED)
    .putRemoteConfigDefault(PDF_RESOURCES_CACHING_ENABLED)
    .putRemoteConfigDefault(PDF_COMPRESSION_LEVEL)
    .putRemoteConfigDefault(COLOR_PICKER_SIZE)
    .putRemoteConfigDefault(OUTLINED_FIELD_DROP_MENU_OFFSET)
    .build()

val TEMPLATE_PREVIEW_DEBOUNCE = LocalConfig.Long("TEMPLATE_PREVIEW_DEBOUNCE", 500L)
val MAX_LAYOUT_COLUMN_WIDTH = LocalConfig.Int("MAX_LAYOUT_COLUMN_WIDTH", 500)
val MIN_LAYOUT_COLUMN_WIDTH = LocalConfig.Int("MIN_LAYOUT_COLUMN_WIDTH", 280)

@Suppress("unused")
val REPORTER_LOCAL_CONFIGS: ImmutableMap<String, LocalConfig<*>> = ImmutableMap.builder<String, LocalConfig<*>>()
    .putLocalConfig(TEMPLATE_PREVIEW_DEBOUNCE)
    .putLocalConfig(MAX_LAYOUT_COLUMN_WIDTH)
    .putLocalConfig(MIN_LAYOUT_COLUMN_WIDTH)
    .build()