package dz.nexatech.reporter.client.core

import kotlinx.coroutines.CompletableDeferred

const val VALUE_TABLE = "value"
const val VALUE_COLUMN_NAMESPACE = VALUE_TABLE + "_namespace"
const val VALUE_COLUMN_INDEX = VALUE_TABLE + "_index"
const val VALUE_COLUMN_NAME = VALUE_TABLE + "_name"
const val VALUE_COLUMN_LAST_UPDATE = "last_update"
const val VALUE_COLUMN_CONTENT = "content"

abstract class AbstractValue protected constructor() {

    abstract val namespace: String
    abstract val index: Int
    abstract val name: String
    abstract val lastUpdate: Long
    abstract val content: String

    // this should typically be "$namespace.$name"
    abstract val key: String

    override fun toString() =
        "Value(namespace='$namespace', index=$index, name='$name', content='$content')"
}

abstract class AbstractInputRepository {
    abstract fun execute(operation: ValueOperation)
}

sealed class ValueOperation {
    class Delete(val namespace: String, val index: Int, val name: String) : ValueOperation() {
        override fun toString() = "Value.Delete(namespace='$namespace', name='$name'"
    }

    class DeleteAll(val values: List<AbstractValue>): ValueOperation() {
        override fun toString() = "Value.DeleteAll(values=$values)"
    }

    class ReplaceAll(val newValues: List<AbstractValue>, val oldValues: List<AbstractValue>): ValueOperation() {
        override fun toString() = "Value.ReplaceAll(newValues=$newValues,oldValues=$oldValues)"
    }

    class UpdateValue(val namespace: String, val index: Int, val name: String, val newValue: String) :
        ValueOperation() {
        override fun toString() =
            "Value.UpdateValue(namespace='$namespace', index=$index, name='$name', newValue=$newValue)"
    }

    class SaveAll(val values: List<AbstractValue>) : ValueOperation() {
        override fun toString(): String =
            "Value.SaveAll(values=$values)"
    }

    class Read(
        val completableDeferred: CompletableDeferred<List<AbstractValue>> = CompletableDeferred(),
        val reader: suspend (Any) -> List<AbstractValue>,
    ) : ValueOperation() {

        suspend fun await() = completableDeferred.await()

        override fun toString() =
            "Value.Read()"
    }
}