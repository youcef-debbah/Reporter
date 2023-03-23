package com.reporter.client.model

import com.google.common.collect.ImmutableMap

val ALL_REMOTE_REPORTER_CONFIGS: ImmutableMap<String, Any> = ImmutableMap.builder<String, Any>()
    .build()

//val CONFIG_NAME = LocalConfig("CONFIG_FULL_NAME", "default")

@Suppress("unused")
val ALL_LOCAL_REPORTER_CONFIGS: ImmutableMap<String, Any> = ImmutableMap.builder<String, Any>()
//    .putConfig(CONFIG_NAME)
    .build()