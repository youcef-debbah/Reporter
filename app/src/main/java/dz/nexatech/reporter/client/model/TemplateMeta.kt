package dz.nexatech.reporter.client.model

import androidx.compose.runtime.Immutable
import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableMap
import dz.nexatech.reporter.client.R
import dz.nexatech.reporter.client.common.AbstractLocalizer
import dz.nexatech.reporter.client.common.AbstractTeller
import dz.nexatech.reporter.client.common.IntCounter
import dz.nexatech.reporter.client.common.addHash
import dz.nexatech.reporter.client.common.atomicLazy
import dz.nexatech.reporter.client.model.Variable.ErrorMessage
import dz.nexatech.reporter.client.model.Variable.ErrorMessageChecker
import dz.nexatech.reporter.util.model.Localizer
import dz.nexatech.reporter.util.ui.AbstractApplication
import org.json.JSONObject
import java.util.Calendar

@Immutable
class TemplateMeta private constructor(
    val template: String,
    val errorCode: Int,
    val sections: ImmutableList<Section> = ImmutableList.of(),
    val records: ImmutableMap<String, Record> = ImmutableMap.of(),
    val teller: AbstractTeller,
) {
    fun hasErrors() = errorCode != 0

    private val hash by atomicLazy {
        records.values.hashCode().addHash(sections).addHash(template)
    }

    override fun hashCode() = hash

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TemplateMeta

        if (template != other.template) return false
        if (sections != other.sections) return false
        if (records != other.records) return false

        return true
    }

    override fun toString() =
        "TemplateMeta(template=$template, sections=$sections, records=$records)"

    companion object {
        fun from(
            templateName: String,
            json: String,
            teller: AbstractTeller,
            localizer: AbstractLocalizer,
        ): TemplateMeta {
            if (json.isEmpty())
                return TemplateMeta(template = templateName, errorCode = 404, teller = teller)

            try {
                val jsonObject = JSONObject(json)
                val errorCode = IntCounter()
                val sections =
                    buildSections(templateName, json, jsonObject, errorCode, teller, localizer)
                val records =
                    buildRecords(templateName, json, jsonObject, errorCode, teller, localizer)
                return TemplateMeta(
                    templateName,
                    errorCode.value,
                    sections,
                    records,
                    teller,
                )
            } catch (e: Exception) {
                teller.warn("invalid template meta for '$templateName': $json", e)
                return TemplateMeta(template = templateName, errorCode = 400, teller = teller)
            }
        }

        private fun buildSections(
            templateName: String,
            json: String,
            jsonObject: JSONObject,
            errorCode: IntCounter,
            teller: AbstractTeller,
            localizer: AbstractLocalizer,
        ): ImmutableList<Section> {
            try {
                val sectionsJsonArray = jsonObject.getJSONArray("sections")
                val sectionsBuilder = ImmutableList.builder<Section>()
                for (i in 0 until sectionsJsonArray.length()) {
                    try {
                        val sectionJsonObject = sectionsJsonArray.getJSONObject(i)
                        val variablesJsonArray = sectionJsonObject.getJSONArray("variables")
                        val variablesBuilder: ImmutableList.Builder<Variable> =
                            ImmutableList.builder()

                        for (j in 0 until variablesJsonArray.length()) {
                            val variableJsonObject = variablesJsonArray.getJSONObject(j)
                            val name = variableJsonObject.getString("name")
                            val variable = Variable(
                                templateName,
                                name,
                                variableJsonObject.optBoolean("required"),
                                variableJsonObject.getString("type"),
                                variableJsonObject.optString("icon"),
                                variableJsonObject.optLong("min"),
                                variableJsonObject.getLong("max"),
                                variableJsonObject.optString("prefix_ar"),
                                variableJsonObject.optString("prefix_fr"),
                                variableJsonObject.optString("prefix_en"),
                                variableJsonObject.optString("suffix_ar"),
                                variableJsonObject.optString("suffix_fr"),
                                variableJsonObject.optString("suffix_en"),
                                variableJsonObject.optString("default"),
                                variableJsonObject.getString("label_ar"),
                                variableJsonObject.getString("label_fr"),
                                variableJsonObject.getString("label_en"),
                                variableJsonObject.getString("desc_ar"),
                                variableJsonObject.getString("desc_fr"),
                                variableJsonObject.getString("desc_en"),
                                localizer,
                            )

                            variablesBuilder.add(variable)
                        }

                        sectionsBuilder.add(
                            Section(
                                templateName,
                                sectionJsonObject.optString("icon"),
                                sectionJsonObject.getString("label_ar"),
                                sectionJsonObject.getString("label_fr"),
                                sectionJsonObject.getString("label_en"),
                                sectionJsonObject.getString("desc_ar"),
                                sectionJsonObject.getString("desc_fr"),
                                sectionJsonObject.getString("desc_en"),
                                variablesBuilder.build(),
                                localizer,
                            )
                        )
                    } catch (e: Exception) {
                        teller.warn("invalid template section#$i for '$templateName': $json", e)
                        errorCode.dec()
                        errorCode.addFlag(1)
                    }
                }
                return sectionsBuilder.build()
            } catch (e: Exception) {
                teller.warn("invalid template sections for '$templateName': $json", e)
                errorCode.addFlag(1)
                return ImmutableList.of()
            }
        }

        private fun buildRecords(
            templateName: String,
            json: String,
            jsonObject: JSONObject,
            errorCode: IntCounter,
            teller: AbstractTeller,
            localizer: AbstractLocalizer,
        ): ImmutableMap<String, Record> {
            try {
                val recordsJsonArray = jsonObject.optJSONArray("records")
                if (recordsJsonArray != null) {
                    val recordsBuilder = ImmutableMap.builder<String, Record>()
                    for (recordIndex in 0 until recordsJsonArray.length()) {
                        try {
                            val recordJsonObject = recordsJsonArray.getJSONObject(recordIndex)
                            val variablesJsonArray = recordJsonObject.getJSONArray("variables")
                            val variablesBuilder: ImmutableList.Builder<Variable> =
                                ImmutableList.builder()

                            val recordName = recordJsonObject.getString("name")
                            val recordNamespace = Record.namespace(templateName, recordName)

                            for (varIndex in 0 until variablesJsonArray.length()) {
                                val variableJsonObject = variablesJsonArray.getJSONObject(varIndex)
                                val name = variableJsonObject.getString("name")
                                val variable = Variable(
                                    recordNamespace,
                                    name,
                                    variableJsonObject.optBoolean("required"),
                                    variableJsonObject.getString("type"),
                                    variableJsonObject.optString("icon"),
                                    variableJsonObject.optLong("min"),
                                    variableJsonObject.getLong("max"),
                                    variableJsonObject.optString("prefix_ar"),
                                    variableJsonObject.optString("prefix_fr"),
                                    variableJsonObject.optString("prefix_en"),
                                    variableJsonObject.optString("suffix_ar"),
                                    variableJsonObject.optString("suffix_fr"),
                                    variableJsonObject.optString("suffix_en"),
                                    variableJsonObject.optString("default"),
                                    variableJsonObject.getString("label_ar"),
                                    variableJsonObject.getString("label_fr"),
                                    variableJsonObject.getString("label_en"),
                                    variableJsonObject.getString("desc_ar"),
                                    variableJsonObject.getString("desc_fr"),
                                    variableJsonObject.getString("desc_en"),
                                    localizer,
                                )
                                variablesBuilder.add(variable)
                            }

                            recordsBuilder.put(
                                recordName,
                                Record(
                                    recordName,
                                    recordNamespace,
                                    recordJsonObject.optString("icon"),
                                    recordJsonObject.getString("label_ar"),
                                    recordJsonObject.getString("label_fr"),
                                    recordJsonObject.getString("label_en"),
                                    recordJsonObject.getString("desc_ar"),
                                    recordJsonObject.getString("desc_fr"),
                                    recordJsonObject.getString("desc_en"),
                                    variablesBuilder.build(),
                                    localizer,
                                )
                            )
                        } catch (e: Exception) {
                            teller.warn(
                                "invalid template record#$recordIndex for '$templateName': $json",
                                e
                            )
                            errorCode.dec()
                            errorCode.addFlag(2)
                        }
                    }
                    return recordsBuilder.build()
                }
            } catch (e: Exception) {
                teller.warn("invalid template records for '$templateName': $json", e)
                errorCode.addFlag(2)
            }

            return ImmutableMap.of()
        }
    }
}

@Immutable
abstract class Form(
    val namespace: String,
    val icon: String,
    val label_ar: String,
    val label_fr: String,
    val label_en: String,
    val desc_ar: String,
    val desc_fr: String,
    val desc_en: String,
    val variables: ImmutableList<Variable>,
    val localizer: AbstractLocalizer,
) {

    val className: String = this.javaClass.simpleName

    val label by atomicLazy {
        localizer.inPrimaryLang(label_en, label_fr, label_ar)
    }

    val desc by atomicLazy {
        localizer.inPrimaryLang(desc_en, desc_fr, desc_ar)
    }

    private val hash: Int by atomicLazy {
        variables.hashCode()
            .addHash(label_ar)
            .addHash(label_fr)
            .addHash(label_en)
            .addHash(desc_ar)
            .addHash(desc_fr)
            .addHash(desc_en)
            .addHash(namespace)
            .addHash(icon)
    }

    final override fun hashCode(): Int = hash

    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Record

        if (namespace != other.namespace) return false
        if (icon != other.icon) return false
        if (label_ar != other.label_ar) return false
        if (label_fr != other.label_fr) return false
        if (label_en != other.label_en) return false
        if (desc_ar != other.desc_ar) return false
        if (desc_fr != other.desc_fr) return false
        if (desc_en != other.desc_en) return false
        if (variables != other.variables) return false

        return true
    }

    override fun toString() =
        "$className(namespace='$namespace', label='$label', variables=$variables')"

    @Suppress("unused")
    open fun debug() =
        "$className(namespace='$namespace', icon='$icon', label_ar='$label_ar', label_fr='$label_fr', label_en='$label_en', desc_ar='$desc_ar', desc_fr='$desc_fr', desc_en='$desc_en', variables=$variables)"
}

@Immutable
class Record(
    val name: String,
    namespace: String,
    icon: String,
    label_ar: String,
    label_fr: String,
    label_en: String,
    desc_ar: String,
    desc_fr: String,
    desc_en: String,
    variables: ImmutableList<Variable>,
    localizer: AbstractLocalizer,
) : Form(
    namespace,
    icon,
    label_ar,
    label_fr,
    label_en,
    desc_ar,
    desc_fr,
    desc_en,
    variables,
    localizer
) {
    companion object {
        const val NAMESPACE_SEPARATOR: String = "@"
        fun namespace(template: String, recordName: String) =
            "$template$NAMESPACE_SEPARATOR$recordName"
    }
}

@Immutable
class Section(
    namespace: String,
    icon: String,
    label_ar: String,
    label_fr: String,
    label_en: String,
    desc_ar: String,
    desc_fr: String,
    desc_en: String,
    variables: ImmutableList<Variable>,
    localizer: AbstractLocalizer,
) : Form(
    namespace,
    icon,
    label_ar,
    label_fr,
    label_en,
    desc_ar,
    desc_fr,
    desc_en,
    variables,
    localizer
)

@Immutable
class Variable internal constructor(
    val namespace: String,
    val name: String,
    val required: Boolean,
    val type: String,
    val icon: String,
    val min: Long,
    val max: Long,
    val prefix_ar: String,
    val prefix_fr: String,
    val prefix_en: String,
    val suffix_ar: String,
    val suffix_fr: String,
    val suffix_en: String,
    val default: String,
    val label_ar: String,
    val label_fr: String,
    val label_en: String,
    val desc_ar: String,
    val desc_fr: String,
    val desc_en: String,
    val localizer: AbstractLocalizer,
) {

    companion object {
        const val SECTION_VARIABLE_INDEX: Int = -1
        private val resources = AbstractApplication.INSTANCE.resources
        fun key(namespace: String, name: String) = "${namespace}.$name"
    }

    private val inputRequiredMessage =
        ErrorMessage { resources.getString(R.string.input_required) }
    private val inputTooLongMessage =
        ErrorMessage { resources.getString(R.string.input_too_long, max) }
    private val inputTooShortMessage =
        ErrorMessage { resources.getString(R.string.input_too_short, min) }

    private val minDate = Type.Date.formatTemplateDate(min)
    private val maxDate = Type.Date.formatTemplateDate(max)

    private val dateTooLateMessage =
        ErrorMessage { resources.getString(R.string.date_too_late, maxDate) }
    private val dateTooEarlyMessage =
        ErrorMessage { resources.getString(R.string.date_too_early, minDate) }

    //    private val errorMessageChecker = ErrorMessageChecker.forType(type)
    private val errorMessageChecker = Type.Text.checker  // TODO remove

    fun errorMessage(value: String) = errorMessageChecker.check(this, value)

    fun interface ErrorMessage {
        fun asString(input: String): String
    }

    fun interface ErrorMessageChecker {
        fun check(variable: Variable, value: String): ErrorMessage?

        companion object {
            fun forType(type: String) = when (type) {
                Type.Text.name -> Type.Text.checker
                Type.Number.name -> Type.Number.checker
                Type.Counter.name -> Type.Counter.checker
                Type.Decimal.name -> Type.Decimal.checker
                Type.Date.name -> Type.Date.checker
                Type.Switch.name -> Type.Switch.checker
                Type.Color.name -> Type.Color.checker
                Type.Font.name -> Type.Font.checker
                Type.Options.name -> Type.Options.checker
                else -> Type.Unknown.checker
            }
        }
    }

    object Type {
        object Text {
            const val name: String = "text"
            val checker = ErrorMessageChecker { variable, value ->
                val length = value.length
                if (variable.required && length == 0) {
                    variable.inputRequiredMessage
                } else if (length > variable.max) {
                    variable.inputTooLongMessage
                } else if (length < variable.min) {
                    variable.inputTooShortMessage
                } else {
                    null
                }
            }
        }

        object Number {
            const val name: String = "number"
            val checker = ErrorMessageChecker { variable, value ->
                null // TODO
            }
        }

        object Counter {
            const val name: String = "counter"
            val checker = ErrorMessageChecker { variable, value ->
                null // TODO
            }
        }

        object Decimal {
            const val name: String = "decimal"
            val checker = ErrorMessageChecker { variable, value ->
                null // TODO
            }
        }

        object Date {
            const val name: String = "date"

            val checker = ErrorMessageChecker { variable, value ->
                val epoch = parseTemplateDate(value)
                if (variable.required && epoch == null) {
                    variable.inputRequiredMessage
                } else if (epoch != null && epoch > variable.max) {
                    variable.dateTooLateMessage
                } else if (epoch != null && epoch < variable.min) {
                    variable.dateTooEarlyMessage
                } else {
                    null
                }
            }

            fun formatTemplateDate(epoch: Long?): String? {
                if (epoch == null) return null

                val date = Calendar.getInstance().apply {
                    this.timeInMillis = epoch
                }

                return String.format(
                    "%02d %s %04d",
                    date.get(Calendar.DAY_OF_MONTH),
                    Localizer.monthName(date.get(Calendar.MONTH)),
                    date.get(Calendar.YEAR)
                )
            }

            fun parseTemplateDate(templateDate: String): Long? {
                val length = templateDate.length
                if (length > 10) {
                    val day = templateDate.substring(0..1).toIntOrNull()
                    val month = Localizer.monthIndex(templateDate.substring(3..length - 6))
                    val year = templateDate.substring(length - 4 until length).toIntOrNull()
                    if (day != null && month != null && year != null) {
                        return Calendar.getInstance().apply {
                            set(Calendar.YEAR, year)
                            set(Calendar.MONTH, month)
                            set(Calendar.DAY_OF_MONTH, day)
                            set(Calendar.HOUR_OF_DAY, 12)
                            set(Calendar.MINUTE, 0)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }.timeInMillis
                    }
                }

                return null
            }
        }

        object Switch {
            const val name: String = "switch"
            val checker = ErrorMessageChecker { variable, value ->
                null // TODO
            }
        }

        object Color {
            const val name: String = "color"
            const val COLOR_PREFIX = '#'
            val checker = ErrorMessageChecker { variable, value ->
                if (variable.required && parseColor(value) == null) {
                    variable.inputRequiredMessage
                } else {
                    null
                }
            }

            fun formatColor(color: androidx.compose.ui.graphics.Color): String {
                val red = (color.red * 255).toInt()
                val green = (color.green * 255).toInt()
                val blue = (color.blue * 255).toInt()
                return String.format("$COLOR_PREFIX%02X%02X%02X", red, green, blue)
            }

            fun parseColor(value: String): Long? {
                if (value.isEmpty() || value[0] != COLOR_PREFIX) return null
                val stringValue = "FF" + value.removePrefix("#")
                return stringValue.toLongOrNull(16)
            }
        }

        object Font {
            const val name: String = "font"
            val checker = ErrorMessageChecker { variable, value ->
                null // TODO
            }
        }

        object Options {
            const val name: String = "options"
            val checker: ErrorMessageChecker = ErrorMessageChecker { variable, value ->
                null // TODO
            }
        }

        object Unknown {
            const val name: String = "unknown"
            val checker = ErrorMessageChecker { _, _ ->
                null
            }
        }
    }

    val key = key(namespace, name)

    val label by atomicLazy {
        localizer.inPrimaryLang(label_en, label_fr, label_ar)
    }

    val desc by atomicLazy {
        localizer.inPrimaryLang(desc_en, desc_fr, desc_ar)
    }

    val prefix by atomicLazy {
        localizer.inPrimaryLang(prefix_en, prefix_fr, prefix_ar)
    }

    val suffix by atomicLazy {
        localizer.inPrimaryLang(suffix_en, suffix_fr, suffix_ar)
    }

    private val hash by atomicLazy {
        required.hashCode()
            .addHash(type)
            .addHash(icon)
            .addHash(min)
            .addHash(max)
            .addHash(prefix)
            .addHash(suffix)
            .addHash(default)
            .addHash(label_ar)
            .addHash(label_fr)
            .addHash(label_en)
            .addHash(desc_ar)
            .addHash(desc_fr)
            .addHash(desc_en)
            .addHash(namespace)
            .addHash(name)
    }

    override fun hashCode() = hash

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Variable

        if (namespace != other.namespace) return false
        if (name != other.name) return false
        if (required != other.required) return false
        if (type != other.type) return false
        if (icon != other.icon) return false
        if (min != other.min) return false
        if (max != other.max) return false
        if (prefix != other.prefix) return false
        if (suffix != other.suffix) return false
        if (default != other.default) return false
        if (label_ar != other.label_ar) return false
        if (label_fr != other.label_fr) return false
        if (label_en != other.label_en) return false
        if (desc_ar != other.desc_ar) return false
        if (desc_fr != other.desc_fr) return false
        if (desc_en != other.desc_en) return false

        return true
    }

    override fun toString() =
        "Variable(key='$key', type='$type', label='$label', default='$default')"

    @Suppress("unused")
    fun debug() =
        "Variable(key='$key', required=$required, type='$type', icon='$icon', min=$min, max=$max, prefix='$prefix', suffix='$suffix', default='$default', label_ar='$label_ar', label_fr='$label_fr', label_en='$label_en', desc_ar='$desc_ar', desc_fr='$desc_fr', desc_en='$desc_en', label='$label', desc='$desc')"
}