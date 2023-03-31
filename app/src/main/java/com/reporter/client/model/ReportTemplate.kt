package com.reporter.client.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.reporter.util.model.Localizer

const val REPORT_TEMPLATE_TABLE = "report_template"
const val REPORT_TEMPLATE_ID = "report_template_id"

@Entity(tableName = REPORT_TEMPLATE_TABLE)
data class ReportTemplate(
    @PrimaryKey
    @ColumnInfo(name = REPORT_TEMPLATE_ID)
    val id: Long,
    @ColumnInfo(index = true)
    val name: String,
    val content: String,
    val label_en: String?,
    val label_ar: String?,
    val label_fr: String?,
    val desc_en: String?,
    val desc_ar: String?,
    val desc_fr: String?,
) {
    val label by lazy {
        Localizer.inPrimaryLang(label_en, label_ar, label_fr)
    }

    val desc by lazy {
        Localizer.inPrimaryLang(desc_en, desc_ar, desc_fr)
    }
}