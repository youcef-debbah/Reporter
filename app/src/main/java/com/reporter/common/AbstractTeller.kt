package com.reporter.common

abstract class AbstractTeller {
    @PublicAPI
    abstract fun logUnexpectedCondition(tag: String, msg: String)

    @PublicAPI
    abstract fun logRareCondition(tag: String, msg: String)

    @PublicAPI
    abstract fun logEvent(event: String)

    @PublicAPI
    abstract fun logEvent(event: String, bundleBuilder: ParamBuilder.() -> Unit)

    @PublicAPI
    abstract fun test(msg: String)

    @PublicAPI
    abstract fun debug(nanos: Long, msg: String)

    @PublicAPI
    abstract fun debug(msg: String)

    @PublicAPI
    abstract fun info(msg: String)

    @PublicAPI
    abstract fun warn(msg: String)

    @PublicAPI
    abstract fun warn(msg: String, e: Throwable?)

    @PublicAPI
    abstract fun error(msg: String, e: Throwable)
}

interface ParamBuilder {
    fun param(key: String, value: Double)
    fun param(key: String, value: Long)
    fun param(key: String, value: String)
}