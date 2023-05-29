package dz.nexatech.reporter.util.model

import com.google.common.collect.ImmutableMap
import dz.nexatech.reporter.client.common.Texts
import dz.nexatech.reporter.util.ui.AbstractApplication

private val DNS_EXCEPTIONS = arrayOf(
    "java.net.SocketException",
    "java.net.SocketTimeoutException",
    "java.net.UnknownHostException"
)
val UNRECORDED_EXCEPTIONS =
    RemoteConfig.String("UNRECORDED_EXCEPTIONS", DNS_EXCEPTIONS.joinToString(Texts.DATA_SEPARATOR))
val CONFIG_VERSION =
    RemoteConfig.String("CONFIG_VERSION", AbstractApplication.INSTANCE.config.versionName + "-local")

val NAVIGATION_ANIMATION_DURATION = RemoteConfig.Int("NAVIGATION_ANIMATION_DURATION", 500)
val CONFIG_SCREEN_NAVIGATION_ANIMATION_ENABLED = RemoteConfig.Boolean("NAVIGATION_ANIMATION_ENABLED", true)
val DYNAMIC_TONAL_PALETTE_ENABLED = RemoteConfig.Boolean("DYNAMIC_TONAL_PALETTE_ENABLED", true)
val TEMPLATES_DOWNLOADING_LINK = RemoteConfig.String("TEMPLATES_DOWNLOADING_LINK", "https://drive.google.com/drive/folders/17v9MWlNCxS1AUNLK7ZxZLpSMLmRxgOyy?usp=share_link")

val GLOBAL_REMOTE_CONFIG_DEFAULTS: ImmutableMap<String, Any> = ImmutableMap.builder<String, Any>()
    .putRemoteConfigDefault(UNRECORDED_EXCEPTIONS)
    .putRemoteConfigDefault(CONFIG_VERSION)
    .putRemoteConfigDefault(NAVIGATION_ANIMATION_DURATION)
    .putRemoteConfigDefault(CONFIG_SCREEN_NAVIGATION_ANIMATION_ENABLED)
    .putRemoteConfigDefault(DYNAMIC_TONAL_PALETTE_ENABLED)
    .putRemoteConfigDefault(TEMPLATES_DOWNLOADING_LINK)
    .build()

val NO_CONFIG_CACHE = LocalConfig.Boolean("NO_CONFIG_CACHE", false)
val APPLICATION_THEME = LocalConfig.String("APPLICATION_THEME", "BLUE")

@Suppress("unused")
val GLOBAL_LOCAL_CONFIGS: ImmutableMap<String, LocalConfig<*>> = ImmutableMap.builder<String, LocalConfig<*>>()
    .putLocalConfig(NO_CONFIG_CACHE)
    .putLocalConfig(APPLICATION_THEME)
    .build()