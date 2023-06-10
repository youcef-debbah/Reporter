package dz.nexatech.reporter.client.model

import androidx.compose.runtime.Immutable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
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
) : AbstractValue() {
    @Ignore
    override val key: String = Variable.key(namespace, name)
}