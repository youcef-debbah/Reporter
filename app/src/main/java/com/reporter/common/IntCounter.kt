package com.reporter.common

class IntCounter(var value: Int = 0): Comparable<IntCounter> {

    companion object {
        const val BIGGEST_FLAG: Int = 0b100_0000__0000_0000__0000_0000__0000_0000
    }

    fun addFlag(index: Int): IntCounter = apply { value = value or (BIGGEST_FLAG ushr index) }

    fun removeFlag(index: Int): IntCounter = apply { value = value and (BIGGEST_FLAG ushr index).inv() }

    fun inc(): IntCounter {
        value++
        return this
    }

    fun dec(): IntCounter {
        value--
        return this
    }

    operator fun plus(other: IntCounter): IntCounter {
        value += other.value
        return this
    }

    operator fun plus(other: Int): IntCounter {
        value += other
        return this
    }

    operator fun minus(other: IntCounter): IntCounter {
        value -= other.value
        return this
    }

    operator fun minus(other: Int): IntCounter {
        value -= other
        return this
    }

    operator fun times(other: IntCounter): IntCounter {
        value *= other.value
        return this
    }

    operator fun times(other: Int): IntCounter {
        value *= other
        return this
    }

    operator fun div(other: IntCounter): IntCounter {
        value /= other.value
        return this
    }

    operator fun div(other: Int): IntCounter {
        value /= other
        return this
    }

    operator fun rem(other: IntCounter): IntCounter {
        value %= other.value
        return this
    }

    operator fun rem(other: Int): IntCounter {
        value %= other
        return this
    }

    operator fun unaryMinus(): IntCounter {
        value = -value
        return this
    }

    override operator fun compareTo(other: IntCounter): Int {
        return value.compareTo(other.value)
    }
}