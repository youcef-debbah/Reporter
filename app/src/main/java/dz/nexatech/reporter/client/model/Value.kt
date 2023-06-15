package dz.nexatech.reporter.client.model

import androidx.compose.runtime.Immutable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import dz.nexatech.reporter.client.common.addHash
import dz.nexatech.reporter.client.common.fastLazy
import dz.nexatech.reporter.client.core.AbstractValue
import dz.nexatech.reporter.client.core.VALUE_COLUMN_CONTENT
import dz.nexatech.reporter.client.core.VALUE_COLUMN_INDEX
import dz.nexatech.reporter.client.core.VALUE_COLUMN_LAST_UPDATE
import dz.nexatech.reporter.client.core.VALUE_COLUMN_NAME
import dz.nexatech.reporter.client.core.VALUE_COLUMN_NAMESPACE
import dz.nexatech.reporter.client.core.VALUE_TABLE

@Immutable
@Entity(
    tableName = VALUE_TABLE,
    primaryKeys = [
        VALUE_COLUMN_NAMESPACE,
        VALUE_COLUMN_INDEX,
        VALUE_COLUMN_NAME,
    ]
)
class Value(
    @ColumnInfo(VALUE_COLUMN_NAMESPACE)
    override val namespace: String,
    @ColumnInfo(VALUE_COLUMN_INDEX)
    override val index: Int,
    @ColumnInfo(VALUE_COLUMN_NAME)
    override val name: String,
    @ColumnInfo(VALUE_COLUMN_LAST_UPDATE)
    override val lastUpdate: Long,
    @ColumnInfo(VALUE_COLUMN_CONTENT)
    override val content: String,
) : AbstractValue(), Comparable<Value> {
    @Ignore
    override val key: String = Variable.key(namespace, name)

    @Ignore
    private val hash: Int = index.hashCode().addHash(name).addHash(namespace)

    override fun hashCode() = hash

    override fun equals(other: Any?) =
        this === other || (other is AbstractValue
                && this.namespace == other.namespace
                && this.name == other.name
                && this.index == other.index
                )

    override fun compareTo(other: Value): Int {
        if (this === other) {
            return 0
        }

        var result = this.index.compareTo(other.index)

        if (result == 0) {
            result = this.namespace.compareTo(other.namespace)
        }

        if (result == 0) {
            result = this.name.compareTo(other.name)
        }

        return result
    }

    fun copy(
        namespace: String = this.namespace,
        index: Int = this.index,
        name: String = this.name,
        lastUpdate: Long = this.lastUpdate,
        content: String = this.content,
    ) = Value(
        namespace = namespace,
        index = index,
        name = name,
        lastUpdate = lastUpdate,
        content = content
    )
}