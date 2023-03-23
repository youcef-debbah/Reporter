package com.reporter.common

import java.lang.Long.max
import java.util.concurrent.atomic.AtomicLong

object Chaos {

    private const val ID_SEPARATOR: Char = '-'

    // basic setting
    private const val TIME_OFFSET: Int = 21
    private const val UNUSED_BITS: Int = 1

    // main constants
    const val TIME_MASK: Long = (-1L)
        .ushr(TIME_OFFSET)
        .shl(TIME_OFFSET + UNUSED_BITS)
        .ushr(UNUSED_BITS)
    const val COUNTER_MASK: Long = TIME_MASK.inv()
        .shl(UNUSED_BITS)
        .ushr(UNUSED_BITS)

    const val END_OF_TIME: Long = TIME_MASK ushr TIME_OFFSET

//    private val localCounter: ThreadLocal<AtomicLong> = object : ThreadLocal<AtomicLong>() {
//        override fun initialValue(): AtomicLong {
//            return AtomicLong()
//        }
//    }

    private val counter = AtomicLong()

    fun globalID(bucket: Int): String = StringBuilder(32)
        .append(bucket)
        .append(ID_SEPARATOR)
        .append(timestamp())
        .toString()

    fun timestamp() = timestamp(System.currentTimeMillis())

    fun timestamp(time: Long): Long = counter.updateAndGet { current -> max(encodeTime(time), current) + 1 }

    fun maxTimestamp(time: Long) = encodeTime(time) or COUNTER_MASK

    fun minTimestamp(time: Long) = encodeTime(time)

    fun extractTime(timestamp: Long) = (timestamp and TIME_MASK) ushr TIME_OFFSET

    private fun encodeTime(time: Long) = time.shl(TIME_OFFSET) and TIME_MASK

    fun decodeGlobalID(globalID: String): Pair<Int, Long> {
        val separatorIndex = globalID.indexOf(ID_SEPARATOR)
        try {
            return Pair(globalID.substring(0, separatorIndex).toInt(), globalID.substring(separatorIndex + 1).toLong())
        } catch (e: RuntimeException) {
            throw RuntimeException("cannot decode invalid global id: $globalID", e)
        }
    }

    fun primaryKey(): String = timestamp().toString()
}