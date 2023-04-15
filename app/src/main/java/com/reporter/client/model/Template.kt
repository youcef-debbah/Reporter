package com.reporter.client.model

import androidx.compose.runtime.Immutable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.reporter.util.model.Localizer

const val TEMPLATE_TABLE = "template"
const val TEMPLATE_COLUMN_NAME = TEMPLATE_TABLE + "_name"
const val TEMPLATE_COLUMN_LABEL_EN = "label_en"
const val TEMPLATE_COLUMN_LABEL_AR = "label_ar"
const val TEMPLATE_COLUMN_LABEL_FR = "label_fr"
const val TEMPLATE_COLUMN_DESC_EN = "desc_en"
const val TEMPLATE_COLUMN_DESC_AR = "desc_ar"
const val TEMPLATE_COLUMN_DESC_FR = "desc_fr"
const val TEMPLATE_COLUMN_LAST_UPDATE = "last_update"

@Immutable
@Entity(tableName = TEMPLATE_TABLE)
class Template(
    @PrimaryKey
    @ColumnInfo(name = TEMPLATE_COLUMN_NAME)
    val name: String,
    @ColumnInfo(name = TEMPLATE_COLUMN_LABEL_EN)
    val label_en: String?,
    @ColumnInfo(name = TEMPLATE_COLUMN_LABEL_AR)
    val label_ar: String?,
    @ColumnInfo(name = TEMPLATE_COLUMN_LABEL_FR)
    val label_fr: String?,
    @ColumnInfo(name = TEMPLATE_COLUMN_DESC_EN)
    val desc_en: String?,
    @ColumnInfo(name = TEMPLATE_COLUMN_DESC_AR)
    val desc_ar: String?,
    @ColumnInfo(name = TEMPLATE_COLUMN_DESC_FR)
    val desc_fr: String?,
    @ColumnInfo(name = TEMPLATE_COLUMN_LAST_UPDATE)
    val lastUpdate: Long,
) {
    @delegate:Ignore
    val label by lazy {
        Localizer.inPrimaryLang(label_en, label_ar, label_fr)
    }

    @delegate:Ignore
    val desc by lazy {
        Localizer.inPrimaryLang(desc_en, desc_ar, desc_fr)
    }

    override fun equals(other: Any?) =
        this === other || (other is Template && this.name == other.name)

    override fun hashCode() = name.hashCode()

    override fun toString() = "Template(name='$name',label=$label)"
}