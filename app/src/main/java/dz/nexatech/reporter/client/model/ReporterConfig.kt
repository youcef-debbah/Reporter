package dz.nexatech.reporter.client.model

import com.google.common.collect.ImmutableMap
import com.itextpdf.kernel.pdf.CompressionConstants
import dz.nexatech.reporter.util.model.LocalConfig
import dz.nexatech.reporter.util.model.RemoteConfig
import dz.nexatech.reporter.util.model.putLocalConfig
import dz.nexatech.reporter.util.model.putRemoteConfig

val LOCALE_TEMPLATE_PREVIEW_DEBOUNCE = LocalConfig.Long("TEMPLATE_PREVIEW_DEBOUNCE", 500L)
val LOCAL_MIN_TEMPLATES_LIST_WIDTH = LocalConfig.Int("MIN_TEMPLATES_LIST_WIDTH", 500)
val REMOTE_TEMPLATES_LIST_LOADING_ANIMATION_ENABLED =
    RemoteConfig.Boolean("TEMPLATES_LIST_LOADING_ANIMATION_ENABLED", true)
val REMOTE_PDF_RESOURCES_CACHING_ENABLED = RemoteConfig.Boolean("PDF_RESOURCES_CACHING_ENABLED", false)
val REMOTE_PDF_COMPRESSION_LEVEL =
    RemoteConfig.Int("PDF_COMPRESSION_LEVEL", CompressionConstants.BEST_COMPRESSION)

val ALL_REMOTE_REPORTER_CONFIGS: ImmutableMap<String, Any> = ImmutableMap.builder<String, Any>()
    .putRemoteConfig(REMOTE_TEMPLATES_LIST_LOADING_ANIMATION_ENABLED)
    .putRemoteConfig(REMOTE_PDF_RESOURCES_CACHING_ENABLED)
    .putRemoteConfig(REMOTE_PDF_COMPRESSION_LEVEL)
    .build()

@Suppress("unused")
val ALL_LOCAL_REPORTER_CONFIGS: ImmutableMap<String, Any> = ImmutableMap.builder<String, Any>()
    .putLocalConfig(LOCALE_TEMPLATE_PREVIEW_DEBOUNCE)
    .build()