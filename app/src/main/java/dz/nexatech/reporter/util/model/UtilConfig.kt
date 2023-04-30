package dz.nexatech.reporter.util.model

import com.google.common.collect.ImmutableMap
import dz.nexatech.reporter.common.Texts
import dz.nexatech.reporter.util.ui.AbstractApplication

private val DNS_EXCEPTIONS = arrayOf(
    "java.net.SocketException",
    "java.net.SocketTimeoutException",
    "java.net.UnknownHostException"
)
val REMOTE_UNRECORDED_EXCEPTIONS =
    RemoteConfig("UNRECORDED_EXCEPTIONS", DNS_EXCEPTIONS.joinToString(Texts.DATA_SEPARATOR))
val REMOTE_CONFIG_VERSION =
    RemoteConfig("CONFIG_VERSION", AbstractApplication.INSTANCE.config.versionName + "-local")

val REMOTE_NAVIGATION_ANIMATION_DURATION = RemoteConfig("NAVIGATION_ANIMATION_DURATION", 500)
val CONFIG_SCREEN_NAVIGATION_ANIMATION_ENABLED = RemoteConfig<Boolean>("NAVIGATION_ANIMATION_ENABLED", true)
val REMOTE_DYNAMIC_TONAL_PALETTE_ENABLED = RemoteConfig("DYNAMIC_TONAL_PALETTE_ENABLED", true)
val REMOTE_TEMPLATES_DOWNLOADING_LINK = RemoteConfig("TEMPLATES_DOWNLOADING_LINK", "https://drive.google.com/drive/folders/17v9MWlNCxS1AUNLK7ZxZLpSMLmRxgOyy?usp=share_link")

val ALL_REMOTE_GLOBAL_CONFIGS: ImmutableMap<String, Any> = ImmutableMap.builder<String, Any>()
    .putConfig(REMOTE_UNRECORDED_EXCEPTIONS)
    .putConfig(REMOTE_CONFIG_VERSION)
    .putConfig(REMOTE_NAVIGATION_ANIMATION_DURATION)
    .putConfig(CONFIG_SCREEN_NAVIGATION_ANIMATION_ENABLED)
    .putConfig(REMOTE_DYNAMIC_TONAL_PALETTE_ENABLED)
    .putConfig(REMOTE_TEMPLATES_DOWNLOADING_LINK)
    .build()

val LOCAL_NO_CONFIG_CACHE = LocalConfig("NO_CONFIG_CACHE", false)
val LOCAL_APPLICATION_THEME = LocalConfig("APPLICATION_THEME", "BLUE")

@Suppress("unused")
val ALL_LOCAL_GLOBAL_CONFIGS: ImmutableMap<String, Any> = ImmutableMap.builder<String, Any>()
    .putConfig(LOCAL_NO_CONFIG_CACHE)
    .putConfig(LOCAL_APPLICATION_THEME)
    .build()