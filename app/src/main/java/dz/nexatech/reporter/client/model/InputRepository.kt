package dz.nexatech.reporter.client.model

import androidx.compose.runtime.Stable
import dz.nexatech.reporter.client.common.ioLaunch
import dz.nexatech.reporter.client.core.AbstractInputRepository
import dz.nexatech.reporter.client.core.AbstractValue
import dz.nexatech.reporter.client.core.ValueUpdate
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.channels.Channel
import javax.inject.Inject

@Stable
class InputRepository @Inject constructor(
    private val valuesDaoLoader: dagger.Lazy<ValuesDAO>,
) : AbstractInputRepository() {
    private val pendingValuesUpdates = Channel<ValueUpdate>(capacity = Channel.UNLIMITED)
    private val valuesDao = CompletableDeferred<ValuesDAO>()

    init {
        ioLaunch {
            val valuesDAO = valuesDaoLoader.get()
            valuesDao.complete(valuesDAO)
            val pendingUpdates = pendingValuesUpdates
            while (true) {
                val update = pendingUpdates.receive()
                val newContent = update.newContent
                if (newContent == null) {
                    valuesDAO.delete(update.namespace, update.name)
                } else {
                    valuesDAO.insert(update.namespace, update.name, newContent)
                }
            }
        }
    }

    override fun execute(update: ValueUpdate) {
        pendingValuesUpdates.trySend(update)
    }

    suspend fun findValuesByNamespacePrefix(namespacePrefix: String): List<AbstractValue> =
        valuesDao.await().findByNamespacePrefix(namespacePrefix)

    suspend fun delete(namespace: String, name: String) =
        valuesDao.await().delete(namespace, name)
}