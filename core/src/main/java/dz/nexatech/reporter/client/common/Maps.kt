package dz.nexatech.reporter.client.common

import com.google.common.collect.ImmutableMap
import java.util.*
import kotlin.collections.ArrayList

@Suppress("UNCHECKED_CAST")
fun <T: Any> Comparator<T>?.compareSortPosition(o1: T, o2: T): Int =
    this?.compare(o1, o2) ?: (o1 as Comparable<T>).compareTo(o2)

fun <V: Any> ImmutableMap.Builder<String, V>.putValue(value: V): ImmutableMap.Builder<String, V> =
    put(value.toString(), value)

fun <V> MutableMap<String, V>.putValue(value: V): V? = put(value.toString(), value)

fun <K, V> MutableMap<K, V>.calcIfNull(
    key: K,
    mappingFunction: (K) -> V?,
): V? = get(key) ?: mappingFunction.invoke(key)?.also { put(key, it) }

fun <K, V> MutableMap<K, V>.calcIfAbsent(
    key: K,
    mappingFunction: (K) -> V,
): V = get(key) ?: mappingFunction.invoke(key).also { put(key, it) }

fun <V, K : Comparable<K>> NavigableMap<K, V>.closedRange(range: ClosedRange<K>): NavigableMap<K, V> =
    subMap(range.start, true, range.endInclusive, true)

@ExperimentalStdlibApi
fun <V, K : Comparable<K>> NavigableMap<K, V>.openEndRange(range: OpenEndRange<K>): NavigableMap<K, V> =
    subMap(range.start, true, range.endExclusive, false)

fun <T> Collection<T>.slice(count: Int = 2): List<List<T>> {
    val result = ArrayList<ArrayList<T>>(count)
    val sliceSize = (size / count) + 1
    for (i in 0 until count) {
        result.add(ArrayList(sliceSize))
    }

    for ((index, value) in this.withIndex()) {
        result[index % count].add(value)
    }

    return result
}