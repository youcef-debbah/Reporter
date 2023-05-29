package dz.nexatech.reporter.client.common

import com.google.common.collect.ImmutableMap
import com.google.common.collect.MapMaker
import java.util.*
import java.util.concurrent.ConcurrentMap

@Suppress("UNCHECKED_CAST")
fun <T> Comparator<T>?.compareSortPosition(o1: T, o2: T): Int =
    this?.compare(o1, o2) ?: (o1 as Comparable<T>).compareTo(o2)

fun <V : Any> ImmutableMap.Builder<String, V>.putValue(value: V): ImmutableMap.Builder<String, V> =
    put(value.toString(), value)

fun <V> MutableMap<String, V>.putValue(value: V): V? = put(value.toString(), value)

fun <V, K : Comparable<K>> NavigableMap<K, V>.closedRange(range: ClosedRange<K>): NavigableMap<K, V> =
    subMap(range.start, true, range.endInclusive, true)

@ExperimentalStdlibApi
fun <V, K : Comparable<K>> NavigableMap<K, V>.openEndRange(range: OpenEndRange<K>): NavigableMap<K, V> =
    subMap(range.start, true, range.endExclusive, false)

fun <K, V> concurrentMap(init: Int): ConcurrentMap<K, V> =
    MapMaker().concurrencyLevel(64).initialCapacity(init)
        .makeMap()