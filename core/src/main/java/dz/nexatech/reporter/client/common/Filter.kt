package dz.nexatech.reporter.client.common

interface Filter<T> {
    fun isIncluded(value: T): Boolean
}