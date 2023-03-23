package com.reporter.util.model

import com.google.common.collect.ImmutableMap
import com.reporter.common.Texts
import com.reporter.util.ui.AbstractApplication

private val DNS_EXCEPTIONS = arrayOf(
    "java.net.SocketException",
    "java.net.SocketTimeoutException",
    "java.net.UnknownHostException"
)
val REMOTE_UNRECORDED_EXCEPTIONS =
    RemoteConfig("UNRECORDED_EXCEPTIONS", DNS_EXCEPTIONS.joinToString(Texts.DATA_SEPARATOR))
val REMOTE_CONFIG_VERSION =
    RemoteConfig("REMOTE_CONFIG_VERSION", AbstractApplication.INSTANCE.config.versionName + "-local")

val REMOTE_NAVIGATION_ANIMATION_DURATION = RemoteConfig("NAVIGATION_ANIMATION_DURATION", 500)
val REMOTE_CONNECT_TIMEOUT_IN_SECONDS = RemoteConfig("CONNECT_TIMEOUT_IN_SECONDS", 10L)
val REMOTE_READ_TIMEOUT_IN_SECONDS = RemoteConfig("READ_TIMEOUT_IN_SECONDS", 10L)
val REMOTE_WRITE_TIMEOUT_IN_SECONDS = RemoteConfig("WRITE_TIMEOUT_IN_SECONDS", 10L)
val REMOTE_SEARCH_QUERY_DEBOUNCE = RemoteConfig("SEARCH_QUERY_DEBOUNCE", 500L)

val REMOTE_DYNAMIC_COLOR_SCHEME_ENABLED = RemoteConfig("DYNAMIC_COLOR_SCHEME_ENABLED", true)

val ALL_REMOTE_GLOBAL_CONFIGS: ImmutableMap<String, Any> = ImmutableMap.builder<String, Any>()
    .putConfig(REMOTE_UNRECORDED_EXCEPTIONS)
    .putConfig(REMOTE_CONFIG_VERSION)
    .putConfig(REMOTE_NAVIGATION_ANIMATION_DURATION)
    .putConfig(REMOTE_CONNECT_TIMEOUT_IN_SECONDS)
    .putConfig(REMOTE_READ_TIMEOUT_IN_SECONDS)
    .putConfig(REMOTE_WRITE_TIMEOUT_IN_SECONDS)
    .putConfig(REMOTE_SEARCH_QUERY_DEBOUNCE)
    .putConfig(REMOTE_DYNAMIC_COLOR_SCHEME_ENABLED)
    .build()

val LOCAL_NO_CONFIG_CACHE = LocalConfig("NO_CONFIG_CACHE", false)
val LOCAL_APPLICATION_THEME = LocalConfig("APPLICATION_THEME", "")

@Suppress("unused")
val ALL_LOCAL_GLOBAL_CONFIGS: ImmutableMap<String, Any> = ImmutableMap.builder<String, Any>()
    .putConfig(LOCAL_NO_CONFIG_CACHE)
    .putConfig(LOCAL_APPLICATION_THEME)
    .build()