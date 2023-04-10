package com.reporter.client.model

import com.google.common.collect.ImmutableMap
import com.reporter.util.model.RemoteConfig
import com.reporter.util.model.putConfig

val CONFIG_TEMPLATE_PREVIEW_DEBOUNCE = RemoteConfig<Long>("TEMPLATE_PREVIEW_DEBOUNCE", 500)

val ALL_REMOTE_REPORTER_CONFIGS: ImmutableMap<String, Any> = ImmutableMap.builder<String, Any>()
    .putConfig(CONFIG_TEMPLATE_PREVIEW_DEBOUNCE)
    .build()

@Suppress("unused")
val ALL_LOCAL_REPORTER_CONFIGS: ImmutableMap<String, Any> = ImmutableMap.builder<String, Any>()
    .build()