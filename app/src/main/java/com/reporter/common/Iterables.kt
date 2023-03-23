package com.reporter.common

inline fun <T, reified R> Iterable<T>.mapToArray(
    nullValue: R,
    arraySize: Int = if (this is Collection) this.size else 1,
    mapper: (T) -> R,
): Array<R> {
    val result: Array<R> = Array(arraySize) { nullValue }
    var i = 0
    val iterator = iterator()
    while (i < arraySize && iterator.hasNext()) {
        result[i] = mapper(iterator.next())
        i++
    }

    return result
}

fun <T> Iterator<T>.indexDiff(filter1: (T) -> Boolean, filter2: (T) -> Boolean): Int? {
    var index1 = -1
    var index2 = -1
    var i = -1
    for (value in this) {
        i++
        if (index1 == -1 && filter1.invoke(value)) {
            index1 = i
        }
        if (index2 == -1 && filter2.invoke(value)) {
            index2 = i
        }
        if (index1 > -1 && index2 > -1) {
            return index2 - index1
        }
    }

    return null
}