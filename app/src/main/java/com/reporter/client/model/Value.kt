package com.reporter.client.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore

const val VALUE_VARIABLE = "parent_variable_name"
const val VALUE_TEMPLATE = "parent_template_name"

@Entity(tableName = "variable_value", primaryKeys = [VALUE_VARIABLE, VALUE_TEMPLATE])
class Value(
    @ColumnInfo(VALUE_VARIABLE)
    val variable: String,
    @ColumnInfo(VALUE_TEMPLATE)
    val template: String,
    override val value: String,
) : DynamicValue {
    @Ignore
    override val name: String = "${template}_${variable}"

    override fun equals(other: Any?) =
        this === other || (other is Value && this.name == other.name)

    override fun hashCode() = name.hashCode()

    override fun toString() = "Value(name='$name', value='$value')"
}