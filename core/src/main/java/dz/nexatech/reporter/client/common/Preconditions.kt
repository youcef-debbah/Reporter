@file:JvmMultifileClass
@file:JvmName("PreconditionsKt")
@file:OptIn(ExperimentalContracts::class)

package dz.nexatech.reporter.client.common

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * Throws an [IllegalArgumentException] if the [value] is false.
 */
fun require(value: Boolean) {
    contract {
        returns() implies value
    }
    require(value) { "Failed requirement." }
}

/**
 * Throws an [IllegalArgumentException] with the result of calling [lazyMessage] if the [value] is false.
 */
inline fun require(value: Boolean, lazyMessage: () -> Any) {
    contract {
        returns() implies value
    }
    if (!value) {
        val message = lazyMessage()
        throw IllegalArgumentException(message.toString())
    }
}

/**
 * Throws an [IllegalArgumentException] if the [value] is null. Otherwise returns the not null value.
 */
fun <T : Any> requireNotNull(value: T?): T {
    contract {
        returns() implies (value != null)
    }
    return requireNotNull(value) { "Required value was null." }
}

/**
 * Throws an [IllegalArgumentException] with the result of calling [lazyMessage] if the [value] is null. Otherwise
 * returns the not null value.
 */
inline fun <T : Any> requireNotNull(value: T?, lazyMessage: () -> Any): T {
    contract {
        returns() implies (value != null)
    }

    if (value == null) {
        val message = lazyMessage()
        throw IllegalArgumentException(message.toString())
    } else {
        return value
    }
}

/**
 * Throws an [IllegalStateException] if the [value] is false.
 */
fun check(value: Boolean) {
    contract {
        returns() implies value
    }
    check(value) { "Check failed." }
}

/**
 * Throws an [IllegalStateException] with the result of calling [lazyMessage] if the [value] is false.
 */
inline fun check(value: Boolean, lazyMessage: () -> Any) {
    contract {
        returns() implies value
    }
    if (!value) {
        val message = lazyMessage()
        throw IllegalStateException(message.toString())
    }
}

/**
 * Throws an [IllegalStateException] if the [value] is null. Otherwise
 * returns the not null value.
 */
fun <T : Any> checkNotNull(value: T?): T {
    contract {
        returns() implies (value != null)
    }
    return checkNotNull(value) { "Required value was null." }
}

/**
 * Throws an [IllegalStateException] with the result of calling [lazyMessage]  if the [value] is null. Otherwise
 * returns the not null value.
 */
inline fun <T : Any> checkNotNull(value: T?, lazyMessage: () -> Any): T {
    contract {
        returns() implies (value != null)
    }

    if (value == null) {
        val message = lazyMessage()
        throw IllegalStateException(message.toString())
    } else {
        return value
    }
}


/**
 * Throws an [IllegalStateException] with the given [message].
 */
fun error(message: Any): Nothing = throw IllegalStateException(message.toString())
