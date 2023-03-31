package com.reporter.client.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import com.reporter.util.model.Localizer

const val REPORT_TEMPLATE_TABLE = "report_template"
const val REPORT_TEMPLATE_ID = "report_template_id"
const val REPORT_TEMPLATE_NAME = "name"

@Entity(
    tableName = REPORT_TEMPLATE_TABLE,
    indices = [Index(value = [REPORT_TEMPLATE_NAME], unique = true)]
)
data class ReportTemplate(
    @PrimaryKey
    @ColumnInfo(name = REPORT_TEMPLATE_ID)
    val id: Long,
    @ColumnInfo(name = REPORT_TEMPLATE_NAME)
    val name: String,
    val content: String,
    val label_en: String?,
    val label_ar: String?,
    val label_fr: String?,
    val desc_en: String?,
    val desc_ar: String?,
    val desc_fr: String?,
) {
    @delegate:Ignore
    val label by lazy {
        Localizer.inPrimaryLang(label_en, label_ar, label_fr)
    }

    @delegate:Ignore
    val desc by lazy {
        Localizer.inPrimaryLang(desc_en, desc_ar, desc_fr)
    }
}