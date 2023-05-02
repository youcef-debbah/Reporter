package dz.nexatech.reporter.client.core

const val VALUE_TABLE = "value"
const val VALUE_COLUMN_NAMESPACE = VALUE_TABLE + "_namespace"
const val VALUE_COLUMN_NAME = "value_name"
const val VALUE_COLUMN_LAST_UPDATE = "last_update"
const val VALUE_COLUMN_CONTENT = "content"

abstract class AbstractValue protected constructor() {

    abstract val namespace: String
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

interface AbstractValuesDAO {

    suspend fun findByNamespacePrefix(namespacePrefix: String): List<AbstractValue>
    suspend fun delete(namespace: String, name: String)
    suspend fun insert(namespace: String, name: String, newContent: String)

    suspend fun execute(update: ValueUpdate) {
        if (update.newContent == null) {
            delete(update.namespace, update.name)
        } else {
            insert(update.namespace, update.name, update.newContent)
        }
    }
}

class ValueUpdate(
    val namespace: String,
    val name: String,
    val newContent: String?,
) {
    override fun toString(): String {
        return "ValueUpdate(namespace='$namespace', name='$name', newContent=$newContent)"
    }
}