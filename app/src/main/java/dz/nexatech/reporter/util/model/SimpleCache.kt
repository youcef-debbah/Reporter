package dz.nexatech.reporter.util.model

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class SimpleCache<T> {

    private val mutex = Mutex()
    private var cache: MutableMap<String, T> = HashMap()

    suspend fun load(key: String, provider: suspend () -> T?): T? = mutex.withLock {
        cache[key] ?: loadNewValue(provider, key)
    }

    private suspend fun loadNewValue(provider: suspend () -> T?, key: String): T? {
        val newValue = provider()
        if (newValue != null) {
            cache[key] = newValue
        }
        return newValue
    }

    suspend fun clear() = mutex.withLock {
        cache = HashMap(cache.size)
    }
}
