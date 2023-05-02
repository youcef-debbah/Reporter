package dz.nexatech.reporter.client.common

import com.google.common.collect.ImmutableSet
import kotlinx.coroutines.sync.withLock

class ReadWriteHashMap<K, V>(
    initialCapacity: Int = 16,
    loadFactor: Float = 0.75f,
) {
    private val map = HashMap<K, V>(initialCapacity, loadFactor)
    private val mutex = ReadWriteMutex()

    suspend fun size(): Int = mutex.read.withLock { map.size }

    suspend fun containsKey(key: K): Boolean = mutex.read.withLock {
        map.containsKey(key)
    }

    suspend fun containsValue(value: V): Boolean = mutex.read.withLock {
        map.containsValue(value)
    }

    suspend fun get(key: K): V? = mutex.read.withLock {
        map[key]
    }

    suspend fun isEmpty(): Boolean = mutex.read.withLock {
        map.isEmpty()
    }

    suspend fun keys(): ImmutableSet<K> = mutex.read.withLock {
        ImmutableSet.copyOf(map.keys)
    }

    suspend fun values(): MutableCollection<V> = mutex.read.withLock {
        ImmutableSet.copyOf(map.values)
    }

    suspend fun clear() {
        mutex.write.withLock {
            map.clear()
        }
    }

    suspend fun put(key: K, value: V): V? = mutex.write.withLock { map.put(key, value) }

    suspend fun putAll(from: Map<out K, V>) = mutex.write.withLock { map.putAll(from) }

    suspend fun remove(key: K): V? = mutex.write.withLock { map.remove(key) }

    suspend fun readEach(action: (Map.Entry<K, V>) -> Unit) {
        mutex.read.withLock {
            map.forEach(action)
        }
    }

    suspend fun calcIfNull(
        key: K,
        mappingFunction: (K) -> V?,
    ): V? = get(key) ?: mutex.write.withLock {
        get(key) ?: mappingFunction.invoke(key)?.also { put(key, it) }
    }

    suspend fun calcIfAbsent(
        key: K,
        mappingFunction: (K) -> V,
    ): V = get(key) ?: mutex.write.withLock {
        get(key) ?: mappingFunction.invoke(key).also { put(key, it) }
    }
}