package dz.nexatech.reporter.client.model

import androidx.compose.runtime.Immutable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import dz.nexatech.reporter.client.common.atomicLazy
import dz.nexatech.reporter.client.core.AbstractTemplate
import dz.nexatech.reporter.client.core.TEMPLATE_COLUMN_DESC_AR
import dz.nexatech.reporter.client.core.TEMPLATE_COLUMN_DESC_EN
import dz.nexatech.reporter.client.core.TEMPLATE_COLUMN_DESC_FR
import dz.nexatech.reporter.client.core.TEMPLATE_COLUMN_LABEL_AR
import dz.nexatech.reporter.client.core.TEMPLATE_COLUMN_LABEL_EN
import dz.nexatech.reporter.client.core.TEMPLATE_COLUMN_LABEL_FR
import dz.nexatech.reporter.client.core.TEMPLATE_COLUMN_LAST_UPDATE
import dz.nexatech.reporter.client.core.TEMPLATE_COLUMN_NAME
import dz.nexatech.reporter.client.core.TEMPLATE_TABLE
import dz.nexatech.reporter.util.model.Localizer

@Immutable
@Entity(tableName = TEMPLATE_TABLE)
class Template(
    @PrimaryKey
    @ColumnInfo(name = TEMPLATE_COLUMN_NAME)
    override val name: String,
    @ColumnInfo(name = TEMPLATE_COLUMN_LABEL_EN)
    override val label_en: String?,
    @ColumnInfo(name = TEMPLATE_COLUMN_LABEL_AR)
    override val label_ar: String?,
    @ColumnInfo(name = TEMPLATE_COLUMN_LABEL_FR)
    override val label_fr: String?,
    @ColumnInfo(name = TEMPLATE_COLUMN_DESC_EN)
    override val desc_en: String?,
    @ColumnInfo(name = TEMPLATE_COLUMN_DESC_AR)
    override val desc_ar: String?,
    @ColumnInfo(name = TEMPLATE_COLUMN_DESC_FR)
    override val desc_fr: String?,
    @ColumnInfo(name = TEMPLATE_COLUMN_LAST_UPDATE)
    override val lastUpdate: Long,
) : AbstractTemplate() {

    companion object {
        fun from(source: AbstractTemplate): Template {
            if (source is Template) {
                return source
            } else {
                return Template(
                    name = source.name,
                    label_en = source.label_en,
                    label_ar = source.label_ar,
                    label_fr = source.label_fr,
                    desc_en = source.desc_en,
                    desc_ar = source.desc_ar,
                    desc_fr = source.desc_fr,
                    lastUpdate = source.lastUpdate,
                )
            }
        }
    }

    @delegate:Ignore
    override val label by atomicLazy {
        Localizer.inPrimaryLang(label_en, label_fr, label_ar)
    }

    @delegate:Ignore
    override val desc by atomicLazy {
        Localizer.inPrimaryLang(desc_en, desc_fr, desc_ar)
    }
}