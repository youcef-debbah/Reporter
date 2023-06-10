package dz.nexatech.reporter.client.core

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

    final override fun equals(other: Any?) =
        this === other || (other is AbstractValue && this.key == other.key)

    final override fun hashCode() = key.hashCode()

    override fun toString() = "Value(name='$key', content='$content')"
}

abstract class AbstractInputRepository {
    abstract fun execute(operation: ValueOperation)
}

sealed class ValueOperation {
    class Delete(val namespace: String, val index: Int, val name: String) : ValueOperation() {
        override fun toString() = "Delete(namespace='$namespace', name='$name'"
    }

    class DeleteByNamespace(val namespace: String) : ValueOperation() {
        override fun toString() = "DeleteAll(namespace='$namespace')"
    }

    class Update(val namespace: String, val index: Int, val name: String, val newContent: String) :
        ValueOperation() {
        override fun toString() =
            "ValueUpdate(namespace='$namespace', name='$name', newContent=$newContent)"
    }
}