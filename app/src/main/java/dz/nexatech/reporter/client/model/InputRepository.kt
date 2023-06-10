package dz.nexatech.reporter.client.model

import androidx.compose.runtime.Stable
import dz.nexatech.reporter.client.common.duration
import dz.nexatech.reporter.client.common.ioLaunch
import dz.nexatech.reporter.client.core.AbstractInputRepository
import dz.nexatech.reporter.client.core.AbstractValue
import dz.nexatech.reporter.client.core.ValueOperation
import dz.nexatech.reporter.util.model.AppConfig
import dz.nexatech.reporter.util.model.Teller
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import javax.inject.Inject

@Stable
class InputRepository @Inject constructor(
    private val valuesDaoLoader: dagger.Lazy<ValuesDAO>,
) : AbstractInputRepository() {
    private val pendingValuesOperations = Channel<ValueOperation>(capacity = Channel.UNLIMITED)
    private val valuesDao = CompletableDeferred<ValuesDAO>()

    init {
        ioLaunch {
            val valuesDAO = valuesDaoLoader.get()
            valuesDao.complete(valuesDAO)
            val pendingUpdates = pendingValuesOperations
            while (true) {
                executeUpdate(pendingUpdates.receive(), valuesDAO)
            }
        }
    }

    private suspend fun executeUpdate(
        operation: ValueOperation,
        valuesDAO: ValuesDAO,
    ) {
        val execution = System.nanoTime()
        when (operation) {
            is ValueOperation.Update -> valuesDAO.insert(
                operation.namespace,
                operation.index,
                operation.name,
                operation.newContent
            )

            is ValueOperation.Delete -> valuesDAO.delete(
                operation.namespace,
                operation.index,
                operation.name
            )

            is ValueOperation.DeleteByNamespace -> valuesDAO.delete(operation.namespace)
        }
        Teller.test(operation.toString() + " done in: " + execution.duration())
    }

    override fun execute(operation: ValueOperation) {
        pendingValuesOperations.trySend(operation)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun valuesDAO(): ValuesDAO {
        while (!pendingValuesOperations.isEmpty) {// TODO consider updating
            delay(AppConfig.get(VALUES_ACTIVE_WAIT_DELAY))
        }
        return valuesDao.await()
    }

    suspend fun loadSectionVariablesValues(template: String): List<AbstractValue> =
        valuesDAO().loadSectionVariablesValues(template)

    suspend fun loadRecordsVariablesValues(template: String): List<AbstractValue> =
        valuesDAO().loadRecordsVariablesValues(template)
}