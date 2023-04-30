package dz.nexatech.reporter.common

import java.util.*

private const val DEFAULT_MAX = Integer.MAX_VALUE

interface FilteredIterator<E> : Iterator<E> {
    fun hasNextUnbounded(): Boolean
}

private class SimpleFilteredIterator<E>(
    iterable: Iterable<E>,
    max: Int = DEFAULT_MAX,
    private val filter: Filter<E>? = null,
) : FilteredIterator<E> {

    private val iterator: Iterator<E> = iterable.iterator()

    private var remaining = if (max >= 0) max else throw IllegalArgumentException("expected: max >= 0; but max=$max")

    private var hasNext: Boolean = false
    private var hasNextUnbounded: Boolean = false

    private var next: E? = null

    init {
        advance()
    }

    private fun advance() {
        while (remaining > 0 && iterator.hasNext()) {
            val value = iterator.next()
            if (filter?.isIncluded(value) != false) {
                remaining--
                hasNext = true
                hasNextUnbounded = true
                next = value
                return
            }
        }

        hasNext = false
        hasNextUnbounded = lookahead()
        next = null
    }

    private fun lookahead(): Boolean {
        while (iterator.hasNext()) {
            if (filter?.isIncluded(iterator.next()) != false)
                return true
        }
        return false
    }

    override fun hasNext(): Boolean {
        return hasNext
    }

    override fun hasNextUnbounded(): Boolean {
        return hasNextUnbounded
    }

    override fun next(): E {
        if (hasNext) {
            val current = next
            advance()
            return current!!
        } else
            throw NoSuchElementException()
    }
}

private class EmptyFilteredIterator<E> : FilteredIterator<E> {
    override fun hasNextUnbounded() = false

    override fun hasNext() = false

    override fun next() = throw NoSuchElementException("empty iterator")
}

fun <E> emptyFilteredIterator(): FilteredIterator<E> = EmptyFilteredIterator()

fun <E> Iterable<E>.filtered(
    count: Int = DEFAULT_MAX,
    filter: Filter<E>? = null,
): FilteredIterator<E> = SimpleFilteredIterator(this, count, filter)

fun <V> Map<*, V>.valuesFiltered(
    count: Int = DEFAULT_MAX,
    filter: Filter<V>? = null,
): FilteredIterator<V> = SimpleFilteredIterator(values, count, filter)

fun <K> Map<K, *>.keysFiltered(
    count: Int = DEFAULT_MAX,
    filter: Filter<K>? = null,
): FilteredIterator<K> = SimpleFilteredIterator(keys, count, filter)

fun <V> NavigableMap<*, V>.descendingValuesFiltered(
    count: Int = DEFAULT_MAX,
    filter: Filter<V>? = null,
): FilteredIterator<V> = SimpleFilteredIterator(descendingMap().values, count, filter)

fun <K> NavigableMap<K, *>.descendingKeysFiltered(
    count: Int = DEFAULT_MAX,
    filter: Filter<K>? = null,
): FilteredIterator<K> = SimpleFilteredIterator(descendingKeySet(), count, filter)