package dz.nexatech.reporter.common

interface Filter<T> {
    fun isIncluded(value: T): Boolean
}