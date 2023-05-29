package dz.nexatech.reporter.util.model

import com.google.common.collect.ImmutableMap
import dz.nexatech.reporter.client.common.Texts
import dz.nexatech.reporter.util.ui.AbstractApplication

private val DNS_EXCEPTIONS = arrayOf(
    "java.net.SocketException",
    "java.net.SocketTimeoutException",
    "java.net.UnknownHostException"
)
val REMOTE_UNRECORDED_EXCEPTIONS =
    RemoteConfig.String("UNRECORDED_EXCEPTIONS", DNS_EXCEPTIONS.joinToString(Texts.DATA_SEPARATOR))
val REMOTE_CONFIG_VERSION =
    RemoteConfig.String("CONFIG_VERSION", AbstractApplication.INSTANCE.config.versionName + "-local")

val REMOTE_NAVIGATION_ANIMATION_DURATION = RemoteConfig.Int("NAVIGATION_ANIMATION_DURATION", 500)
val CONFIG_SCREEN_NAVIGATION_ANIMATION_ENABLED = RemoteConfig.Boolean("NAVIGATION_ANIMATION_ENABLED", true)
val REMOTE_DYNAMIC_TONAL_PALETTE_ENABLED = RemoteConfig.Boolean("DYNAMIC_TONAL_PALETTE_ENABLED", true)
val REMOTE_TEMPLATES_DOWNLOADING_LINK = RemoteConfig.String("TEMPLATES_DOWNLOADING_LINK", "https://drive.google.com/drive/folders/17v9MWlNCxS1AUNLK7ZxZLpSMLmRxgOyy?usp=share_link")

val GLOBAL_REMOTE_CONFIG_DEFAULTS: ImmutableMap<String, Any> = ImmutableMap.builder<String, Any>()
    .putRemoteConfigDefault(REMOTE_UNRECORDED_EXCEPTIONS)
    .putRemoteConfigDefault(REMOTE_CONFIG_VERSION)
    .putRemoteConfigDefault(REMOTE_NAVIGATION_ANIMATION_DURATION)
    .putRemoteConfigDefault(CONFIG_SCREEN_NAVIGATION_ANIMATION_ENABLED)
    .putRemoteConfigDefault(REMOTE_DYNAMIC_TONAL_PALETTE_ENABLED)
    .putRemoteConfigDefault(REMOTE_TEMPLATES_DOWNLOADING_LINK)
    .build()

val LOCAL_NO_CONFIG_CACHE = LocalConfig.Boolean("NO_CONFIG_CACHE", false)
val LOCAL_APPLICATION_THEME = LocalConfig.String("APPLICATION_THEME", "BLUE")

@Suppress("unused")
val GLOBAL_LOCAL_CONFIGS: ImmutableMap<String, LocalConfig<*>> = ImmutableMap.builder<String, LocalConfig<*>>()
    .putLocalConfig(LOCAL_NO_CONFIG_CACHE)
    .putLocalConfig(LOCAL_APPLICATION_THEME)
    .build()