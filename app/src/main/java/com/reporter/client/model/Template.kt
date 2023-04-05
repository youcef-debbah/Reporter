package com.reporter.client.model

import android.webkit.WebView
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.reporter.common.Texts
import com.reporter.util.model.Localizer

const val TEMPLATE_TABLE = "template"
const val COLUMN_TEMPLATE_NAME = "template_name"
const val COLUMN_CONTENT = "content"
const val COLUMN_LABEL_EN = "label_en"
const val COLUMN_LABEL_AR = "label_ar"
const val COLUMN_LABEL_FR = "label_fr"
const val COLUMN_DESC_EN = "desc_en"
const val COLUMN_DESC_AR = "desc_ar"
const val COLUMN_DESC_FR = "desc_fr"
const val COLUMN_LAST_UPDATE = "last_update"

@Entity(tableName = TEMPLATE_TABLE)
class Template(
    @PrimaryKey
    @ColumnInfo(name = COLUMN_TEMPLATE_NAME)
    val name: String,
    @ColumnInfo(name = COLUMN_CONTENT)
    val content: String,
    @ColumnInfo(name = COLUMN_LABEL_EN)
    val label_en: String?,
    @ColumnInfo(name = COLUMN_LABEL_AR)
    val label_ar: String?,
    @ColumnInfo(name = COLUMN_LABEL_FR)
    val label_fr: String?,
    @ColumnInfo(name = COLUMN_DESC_EN)
    val desc_en: String?,
    @ColumnInfo(name = COLUMN_DESC_AR)
    val desc_ar: String?,
    @ColumnInfo(name = COLUMN_DESC_FR)
    val desc_fr: String?,
    @ColumnInfo(name = COLUMN_LAST_UPDATE)
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

    fun loadContent(webView: WebView) {
        if (content.startsWith(Texts.ASSETS_URL_PREFIX)) {
            webView.loadUrl(content)
        } else {
            webView.loadDataWithBaseURL(
                null,
                content,
                Texts.MEME_TYPE_TEXT_HTML,
                Texts.UTF_8,
                null
            )
        }
    }
}