package dz.nexatech.reporter.client.common

abstract class AbstractTeller {
    abstract fun logUnexpectedCondition(tag: String, msg: String)

    abstract fun logRareCondition(tag: String, msg: String)

    abstract fun logEvent(event: String)

    abstract fun logEvent(event: String, bundleBuilder: ParamBuilder.() -> Unit)

    abstract fun test(msg: String)

    abstract fun debug(nanos: Long, msg: String)

    abstract fun debug(msg: String)

    abstract fun info(msg: String)

    abstract fun warn(msg: String)

    abstract fun warn(msg: String, e: Throwable?)

    abstract fun error(msg: String, e: Throwable)
}

interface ParamBuilder {
    fun param(key: String, value: Double)
    fun param(key: String, value: Long)
    fun param(key: String, value: String)
}