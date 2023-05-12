package dz.nexatech.reporter.util.model

import com.google.common.collect.MapMaker
import java.util.concurrent.ConcurrentMap

class SimpleCache<T> {

    private val cache: ConcurrentMap<String, T> = MapMaker()
        .concurrencyLevel(Runtime.getRuntime().availableProcessors() * 2)
        .makeMap()

    suspend fun load(key: String, provider: suspend () -> T?): T? =
        cache[key] ?: loadNewValue(provider, key)

    private suspend fun loadNewValue(provider: suspend () -> T?, key: String): T? {
        val newValue = provider()
        if (newValue != null) {
            cache[key] = newValue
        }
        return newValue
    }

    fun clear() {
        cache.clear()
    }

}
