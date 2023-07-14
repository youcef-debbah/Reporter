package dz.nexatech.reporter.client.model

import dz.nexatech.reporter.client.common.ioLaunch
import dz.nexatech.reporter.client.core.AbstractInputRepository
import dz.nexatech.reporter.client.core.AbstractValue
import dz.nexatech.reporter.client.core.ValueOperation
import dz.nexatech.reporter.util.model.Teller
import kotlinx.coroutines.channels.Channel
import java.util.TreeSet
import javax.inject.Inject

class InputRepository @Inject constructor(
    private val valuesDaoLoader: dagger.Lazy<ValuesDAO>,
) : AbstractInputRepository() {
    private val pendingValuesOperations = Channel<ValueOperation>(capacity = Channel.UNLIMITED)

    init {
        ioLaunch {
            val valuesDAO = valuesDaoLoader.get()
            val pendingUpdates = pendingValuesOperations
            reindexValues(valuesDAO)
            while (true) {
                executeUpdate(pendingUpdates.receive(), valuesDAO)
            }
        }
    }

    private suspend fun reindexValues(valuesDAO: ValuesDAO) {
        val values = valuesDAO.loadAll()
        val cache = mutableMapOf<String, MutableSet<Value>>()
        for (value in values) {
            cache.compute(value.key) { _, set ->
                val currentSet = set ?: TreeSet<Value>()
                currentSet.apply {
                    if (!add(value)) {
                        Teller.logUnexpectedCondition(
                            "dropped_value",
                            "value dropped because its index is not unique: $value"
                        )
                    }
                }
            }
        }

        val updatedValues = ArrayList<Value>(values.size)
        for (valuesSet in cache.values) {
            var index = 1
            for (value in valuesSet) {
                updatedValues.add(value.copy(index = index++))
            }
        }
        valuesDAO.replaceAll(updatedValues)
    }

    private suspend fun executeUpdate(
        operation: ValueOperation,
        valuesDAO: ValuesDAO,
    ) {
        @Suppress("UNCHECKED_CAST")
        when (operation) {
            is ValueOperation.UpdateValue -> valuesDAO.updateValue(
                namespace = operation.namespace,
                index = operation.index,
                name = operation.name,
                newValue = operation.newValue
            )

            is ValueOperation.SaveAll -> valuesDAO.saveAll(operation.values as List<Value>)

            is ValueOperation.Delete -> valuesDAO.delete(
                namespace = operation.namespace,
                index = operation.index,
                name = operation.name
            )

            is ValueOperation.DeleteAll -> valuesDAO.delete(operation.values as List<Value>)

            is ValueOperation.ReplaceAll -> valuesDAO.replaceAll(
                operation.newValues as List<Value>,
                operation.oldValues as List<Value>,
            )

            is ValueOperation.Read -> operation.completableDeferred.complete(
                operation.reader(
                    valuesDAO
                )
            )
        }
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