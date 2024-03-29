package dz.nexatech.reporter.util.model

import com.google.common.collect.ImmutableMap
import dz.nexatech.reporter.client.common.Texts
import dz.nexatech.reporter.util.ui.AbstractApplication
import dz.nexatech.reporter.util.ui.ThemeColors

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
val TEMPLATES_DOWNLOADING_LINK = RemoteConfig.String("TEMPLATES_DOWNLOADING_LINK", "https://drive.google.com/drive/folders/17v9MWlNCxS1AUNLK7ZxZLpSMLmRxgOyy?usp=share_link")
val DEFAULT_LINE_ELEMENT_WIDTH_LIMIT = RemoteConfig.Int("DEFAULT_LINE_ELEMENT_WIDTH_LIMIT", 380)
val LATEST_VERSION_NAME = RemoteConfig.String("LATEST_VERSION_NAME", AbstractApplication.INSTANCE.config.versionName)
val APP_DOWNLOAD_LINK = RemoteConfig.String("APP_DOWNLOAD_LINK", "https://play.google.com/store/apps/details?id=dz.nexatech.reporter.client")

val GLOBAL_REMOTE_CONFIG_DEFAULTS: ImmutableMap<String, Any> = ImmutableMap.builder<String, Any>()
    .putRemoteConfigDefault(UNRECORDED_EXCEPTIONS)
    .putRemoteConfigDefault(CONFIG_VERSION)
    .putRemoteConfigDefault(NAVIGATION_ANIMATION_DURATION)
    .putRemoteConfigDefault(CONFIG_SCREEN_NAVIGATION_ANIMATION_ENABLED)
    .putRemoteConfigDefault(TEMPLATES_DOWNLOADING_LINK)
    .putRemoteConfigDefault(DEFAULT_LINE_ELEMENT_WIDTH_LIMIT)
    .putRemoteConfigDefault(LATEST_VERSION_NAME)
    .putRemoteConfigDefault(APP_DOWNLOAD_LINK)
    .build()

val NO_CONFIG_CACHE = LocalConfig.Boolean("NO_CONFIG_CACHE", false)
val APPLICATION_THEME = LocalConfig.String("APPLICATION_THEME", ThemeColors.DEFAULT_THEME.name)
val DYNAMIC_APPLICATION_THEME = LocalConfig.Boolean("DYNAMIC_APPLICATION_THEME", false)
val INSTALLATION_ID = LocalConfig.String("INSTALLATION_ID", "N/A")

val GLOBAL_LOCAL_CONFIGS: ImmutableMap<String, LocalConfig<*>> = ImmutableMap.builder<String, LocalConfig<*>>()
    .putLocalConfig(NO_CONFIG_CACHE)
    .putLocalConfig(APPLICATION_THEME)
    .putLocalConfig(DYNAMIC_APPLICATION_THEME)
    .putLocalConfig(INSTALLATION_ID)
    .build()