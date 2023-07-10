package dz.nexatech.reporter.client.common

import kotlin.math.abs

const val MICRO_SEC_IN_NANOS = 1000L
const val MILLI_SEC_IN_NANOS = MICRO_SEC_IN_NANOS * 1000
const val SEC_IN_NANOS = MILLI_SEC_IN_NANOS * 1000

const val SEC_IN_MILLIS = 1000
const val MINUTES_IN_MILLIS = 1000 * 60
const val HOUR_IN_MILLIS = MINUTES_IN_MILLIS * 60
const val DAY_IN_MILLIS = HOUR_IN_MILLIS * 24
const val YEAR_IN_MILLIS: Long = (DAY_IN_MILLIS * 365.242196).toLong()

fun Long.duration(anotherNanoTime: Long = System.nanoTime()): String {
    val nanos: Long = abs(this - anotherNanoTime)
    if (nanos < MICRO_SEC_IN_NANOS)
        return "$nanos ns"
    else if (nanos < MILLI_SEC_IN_NANOS)
        return String.format("%.3f μs", nanos.toFloat() / MICRO_SEC_IN_NANOS)
    else if (nanos < SEC_IN_NANOS)
        return String.format("%.3f ms", nanos.toFloat() / MILLI_SEC_IN_NANOS)
    else
        return String.format("%.3f sec", nanos.toFloat() / SEC_IN_NANOS)
}

fun Any.asDoubleOrNull(): Double? =
    if (this is Number) toDouble() else toString().toDoubleOrNull()