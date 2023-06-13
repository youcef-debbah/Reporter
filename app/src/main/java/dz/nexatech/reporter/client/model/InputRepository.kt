package dz.nexatech.reporter.client.model

import androidx.compose.runtime.Stable
import dz.nexatech.reporter.client.common.duration
import dz.nexatech.reporter.client.common.ioLaunch
import dz.nexatech.reporter.client.core.AbstractInputRepository
import dz.nexatech.reporter.client.core.AbstractValue
import dz.nexatech.reporter.client.core.ValueOperation
import dz.nexatech.reporter.util.model.Teller
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.channels.Channel
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
            is ValueOperation.Read -> operation.completableDeferred.complete(
                operation.reader(
                    valuesDAO
                )
            )
        }
        Teller.test(operation.toString() + " done in: " + execution.duration())
    }

    override fun execute(operation: ValueOperation) {
        pendingValuesOperations.trySend(operation)
    }

    suspend fun loadSectionVariablesValues(template: String): List<AbstractValue> {
        val readOperation = ValueOperation.Read {
            val dao = it as ValuesDAO
            dao.loadSectionVariablesValues(template)
        }
        execute(readOperation)
        return readOperation.await()
    }

    suspend fun loadRecordsVariablesValues(template: String): List<AbstractValue> {
        val readOperation = ValueOperation.Read {
            val dao = it as ValuesDAO
            dao.loadRecordsVariablesValues(template)
        }
        execute(readOperation)
        return readOperation.await()
    }
}