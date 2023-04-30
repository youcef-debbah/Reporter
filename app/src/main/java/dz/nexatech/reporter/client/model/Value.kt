package dz.nexatech.reporter.client.model

import androidx.compose.runtime.Immutable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore

const val VALUE_TABLE = "value"
const val VALUE_COLUMN_NAMESPACE = VALUE_TABLE + "_namespace"
const val VALUE_COLUMN_NAME = "value_name"
const val VALUE_COLUMN_LAST_UPDATE = "last_update"
const val VALUE_COLUMN_CONTENT = "content"

@Immutable
@Entity(tableName = VALUE_TABLE, primaryKeys = [VALUE_COLUMN_NAMESPACE, VALUE_COLUMN_NAME])
class Value(
    @ColumnInfo(VALUE_COLUMN_NAMESPACE)
    val namespace: String,
    @ColumnInfo(VALUE_COLUMN_NAME)
    val name: String,
    @ColumnInfo(VALUE_COLUMN_LAST_UPDATE)
    val lastUpdate: Long,
    @ColumnInfo(VALUE_COLUMN_CONTENT)
    val content: String,
) {
    @Ignore
    val key: String = Variable.key(namespace, name)

    override fun equals(other: Any?) =
        this === other || (other is Value && this.key == other.key)

    override fun hashCode() = key.hashCode()

    override fun toString() = "Value(name='$key', content='$content')"
}