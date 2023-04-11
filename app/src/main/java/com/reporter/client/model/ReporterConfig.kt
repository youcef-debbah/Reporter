package com.reporter.client.model

import com.google.common.collect.ImmutableMap
import com.reporter.util.model.RemoteConfig
import com.reporter.util.model.putConfig

val CONFIG_TEMPLATE_PREVIEW_DEBOUNCE = RemoteConfig("TEMPLATE_PREVIEW_DEBOUNCE", 500L)
val CONFIG_TEMPLATES_LIST_LOADING_ANIMATION_ENABLED = RemoteConfig("TEMPLATES_LIST_LOADING_ANIMATION_ENABLED", true)

val ALL_REMOTE_REPORTER_CONFIGS: ImmutableMap<String, Any> = ImmutableMap.builder<String, Any>()
    .putConfig(CONFIG_TEMPLATE_PREVIEW_DEBOUNCE)
    .putConfig(CONFIG_TEMPLATES_LIST_LOADING_ANIMATION_ENABLED)
    .build()

@Suppress("unused")
val ALL_LOCAL_REPORTER_CONFIGS: ImmutableMap<String, Any> = ImmutableMap.builder<String, Any>()
    .build()