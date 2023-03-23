package com.reporter.common

interface Filter<T> {
    fun isIncluded(value: T): Boolean
}