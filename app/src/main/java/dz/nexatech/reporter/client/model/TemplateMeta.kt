@file:Suppress("PropertyName")

package dz.nexatech.reporter.client.model

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.window.DialogProperties
import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableMap
import com.google.common.collect.ImmutableSet
import dz.nexatech.reporter.client.R
import dz.nexatech.reporter.client.common.AbstractLocalizer
import dz.nexatech.reporter.client.common.AbstractTeller
import dz.nexatech.reporter.client.common.IntCounter
import dz.nexatech.reporter.client.common.addHash
import dz.nexatech.reporter.client.common.atomicLazy
import dz.nexatech.reporter.client.common.splitIntoSet
import dz.nexatech.reporter.client.model.Variable.ErrorMessage
import dz.nexatech.reporter.client.model.Variable.ErrorMessageChecker
import dz.nexatech.reporter.client.ui.FontHandler
import dz.nexatech.reporter.util.ui.AbstractApplication
import dz.nexatech.reporter.util.ui.StaticIcon
import dz.nexatech.reporter.util.ui.iconPath
import org.json.JSONObject
import java.util.Locale
import kotlin.math.min

@Immutable
class TemplateMeta private constructor(
    val template: String,
    val errorCode: Int,
    val locale: Locale,
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

        return records != other.records
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
                return TemplateMeta(
                    template = templateName,
                    errorCode = 404,
                    teller = teller,
                    locale = localizer.locale
                )

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
                    localizer.locale,
                    sections,
                    records,
                    teller,
                )
            } catch (e: Exception) {
                teller.warn("invalid template meta for '$templateName': $json", e)
                return TemplateMeta(
                    template = templateName,
                    errorCode = 400,
                    teller = teller,
                    locale = localizer.locale
                )
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
                            val max = variableJsonObject.getLong("max")
                            val variable = Variable(
                                templateName,
                                name,
                                variableJsonObject.optBoolean("required"),
                                variableJsonObject.getString("type"),
                                variableJsonObject.optString("icon"),
                                min(variableJsonObject.optLong("min"), max),
                                max,
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
                                variableJsonObject.optString("desc_ar"),
                                variableJsonObject.optString("desc_fr"),
                                variableJsonObject.optString("desc_en"),
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
                                sectionJsonObject.optString("desc_ar"),
                                sectionJsonObject.optString("desc_fr"),
                                sectionJsonObject.optString("desc_en"),
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
                                val max = variableJsonObject.getLong("max")
                                val variable = Variable(
                                    recordNamespace,
                                    name,
                                    variableJsonObject.optBoolean("required"),
                                    variableJsonObject.getString("type"),
                                    variableJsonObject.optString("icon"),
                                    min(variableJsonObject.optLong("min"), max),
                                    max,
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
                                    variableJsonObject.optString("desc_ar"),
                                    variableJsonObject.optString("desc_fr"),
                                    variableJsonObject.optString("desc_en"),
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
                                    recordJsonObject.optString("desc_ar"),
                                    recordJsonObject.optString("desc_fr"),
                                    recordJsonObject.optString("desc_en"),
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
    icon: String,
    val label_ar: String,
    val label_fr: String,
    val label_en: String,
    val desc_ar: String,
    val desc_fr: String,
    val desc_en: String,
    val variables: ImmutableList<Variable>,
    val localizer: AbstractLocalizer,
) {

    val iconPath: String = iconPath(icon)

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
        if (iconPath != other.iconPath) return false
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
        "$className(namespace='$namespace', icon='$iconPath', label_ar='$label_ar', label_fr='$label_fr', label_en='$label_en', desc_ar='$desc_ar', desc_fr='$desc_fr', desc_en='$desc_en', variables=$variables)"
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
    icon: String,
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

    val iconPath: String = iconPath(icon)

    private val minDate = localizer.formatSimpleDate(min)
    private val maxDate = localizer.formatSimpleDate(max)

    private val dateTooLateMessage =
        ErrorMessage { resources.getString(R.string.date_too_late, maxDate) }
    private val dateTooEarlyMessage =
        ErrorMessage { resources.getString(R.string.date_too_early, minDate) }

    private val valueTooBigMessage =
        ErrorMessage { resources.getString(R.string.value_too_big, max) }
    private val valueTooSmallMessage =
        ErrorMessage { resources.getString(R.string.value_too_small, min) }

    private val inputTooLongMessage =
        ErrorMessage { resources.getString(R.string.input_too_long, max) }
    private val inputTooShortMessage =
        ErrorMessage { resources.getString(R.string.input_too_short, min) }

    private val tooManyLinesMessage =
        ErrorMessage { resources.getString(R.string.too_many_lines, max) }
    private val tooFewLinesMessage =
        ErrorMessage { resources.getString(R.string.too_few_lines, min) }

    private val inputSelectionCountTooLowMessage =
        ErrorMessage { resources.getString(R.string.selection_too_low, min) }
    private val inputSelectionCountTooHighMessage =
        ErrorMessage { resources.getString(R.string.selection_too_high, max) }

    private val errorMessageChecker = ErrorMessageChecker.forType(type)
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
                Type.Options.name -> Type.Options.checker
                else -> Type.Unknown.checker
            }
        }
    }

    fun isFontVariable(): Boolean =
        type == Type.Options.name && desc == Type.Options.FONTS_LIST_OPTIONS

    interface TextType {
        val name: String
        val defaultIcon: StaticIcon
        val keyboardOptions: KeyboardOptions
        val checker: ErrorMessageChecker
    }

    object Type {

        object Lines {
            val name: String
                get() = "lines"

            val defaultIcon: StaticIcon
                get() = StaticIcon.baseline_notes

            val keyboardOptions = KeyboardOptions()

            val checker = ErrorMessageChecker { variable, value ->
                val linebreaks = value.count { it == '\n' }
                if (variable.required && value.isEmpty()) {
                    inputRequiredMessage
                } else if (linebreaks > variable.max) {
                    variable.tooManyLinesMessage
                } else if (linebreaks < variable.min) {
                    variable.tooFewLinesMessage
                } else {
                    null
                }
            }
        }

        object Text : TextType {
            override val name: String
                get() = "text"

            override val defaultIcon: StaticIcon
                get() = StaticIcon.baseline_keyboard

            override val keyboardOptions = KeyboardOptions()

            override val checker = ErrorMessageChecker { variable, value ->
                val length = value.length
                if (variable.required && length == 0) {
                    inputRequiredMessage
                } else if (length > variable.max) {
                    variable.inputTooLongMessage
                } else if (length < variable.min) {
                    variable.inputTooShortMessage
                } else {
                    null
                }
            }
        }

        object Uri : TextType {
            override val name: String
                get() = "uri"

            override val defaultIcon: StaticIcon
                get() = StaticIcon.baseline_language

            override val keyboardOptions =
                KeyboardOptions(autoCorrect = false, keyboardType = KeyboardType.Uri)

            override val checker = ErrorMessageChecker { variable, value ->
                val length = value.length
                if (variable.required && length == 0) {
                    inputRequiredMessage
                } else if (length > variable.max) {
                    variable.inputTooLongMessage
                } else if (length < variable.min) {
                    variable.inputTooShortMessage
                } else {
                    null
                }
            }
        }

        object LinePhone : TextType {
            override val name: String
                get() = "line-phone"

            override val defaultIcon: StaticIcon
                get() = StaticIcon.baseline_call

            override val keyboardOptions =
                KeyboardOptions(autoCorrect = false, keyboardType = KeyboardType.Phone)

            override val checker = ErrorMessageChecker { variable, value ->
                if (value.isNotEmpty() && invalidLinePhone(value)) {
                    illegalLinePhoneNumberMessage
                } else if (variable.required && value.isEmpty()) {
                    inputRequiredMessage
                } else {
                    null
                }
            }

            private fun invalidLinePhone(mobile: String): Boolean {
                if (mobile.length == 12
                    && mobile[3] == ' '
                    && mobile[6] == ' '
                    && mobile[9] == ' '
                ) {
                    return invalidLinePhone(
                        StringBuilder()
                            .append(mobile[0])
                            .append(mobile[1])
                            .append(mobile[2])
                            .append(mobile[4])
                            .append(mobile[5])
                            .append(mobile[7])
                            .append(mobile[8])
                            .append(mobile[10])
                            .append(mobile[11])
                            .toString()
                    )
                }

                if (mobile.length != 9 || mobile[0] != '0' || mobile[1] == '0') return true
                val number = mobile.toIntOrNull()
                return number == null || number < 0
            }
        }

        object Mobile : TextType {
            override val name: String
                get() = "mobile"

            override val defaultIcon: StaticIcon
                get() = StaticIcon.baseline_call

            override val keyboardOptions =
                KeyboardOptions(autoCorrect = false, keyboardType = KeyboardType.Phone)

            override val checker = ErrorMessageChecker { variable, value ->
                if (value.isNotEmpty() && invalidMobile(value)) {
                    illegalMobileNumberMessage
                } else if (variable.required && value.isEmpty()) {
                    inputRequiredMessage
                } else {
                    null
                }
            }

            private fun invalidMobile(mobile: String): Boolean {
                if (mobile.length == 14
                    && mobile[2] == ' '
                    && mobile[5] == ' '
                    && mobile[8] == ' '
                    && mobile[11] == ' '
                ) {
                    return invalidMobile(
                        StringBuilder()
                            .append(mobile[0])
                            .append(mobile[1])
                            .append(mobile[3])
                            .append(mobile[4])
                            .append(mobile[6])
                            .append(mobile[7])
                            .append(mobile[9])
                            .append(mobile[10])
                            .append(mobile[12])
                            .append(mobile[13])
                            .toString()
                    )
                }

                if (mobile.length != 10 || mobile[0] != '0' || mobile[1] == '0') return true
                val number = mobile.toIntOrNull()
                return number == null || number < 0
            }
        }

        object Email : TextType {
            override val name: String
                get() = "email"

            override val defaultIcon: StaticIcon
                get() = StaticIcon.baseline_email

            override val keyboardOptions =
                KeyboardOptions(autoCorrect = false, keyboardType = KeyboardType.Email)

            override val checker = ErrorMessageChecker { variable, value ->
                if (value.isNotEmpty() && isInvalidEmail(value)) {
                    illegalEmailMessage
                } else if (variable.required && value.isEmpty()) {
                    inputRequiredMessage
                } else {
                    null
                }
            }

            fun isInvalidEmail(email: String): Boolean {
                if (email.length < 5) return true
                val lastIndex = email.length - 1

                var hasAtSymbol = false
                var hasDot = false

                for (i in email.indices) {
                    val c = email[i]
                    if (c == '@') {
                        if (hasAtSymbol || i == 0 || i == lastIndex) {
                            return true
                        }
                        hasAtSymbol = true
                    } else if (c == '.') {
                        if (i == 0 || i == lastIndex) {
                            return true
                        }
                        hasDot = true
                    }
                }

                return !hasAtSymbol || !hasDot
            }
        }

        object Counter : TextType {
            override val name: String = "counter"

            override val defaultIcon: StaticIcon
                get() = StaticIcon.baseline_exposure

            override val keyboardOptions =
                KeyboardOptions(autoCorrect = false, keyboardType = KeyboardType.Number)

            override val checker = ErrorMessageChecker { variable, value ->
                val number = value.toIntOrNull()
                if (value.isNotEmpty() && number == null) {
                    inputIllegalMessage
                } else if (variable.required && number == null) {
                    inputRequiredMessage
                } else if (number != null && number > variable.max) {
                    variable.valueTooBigMessage
                } else if (number != null && number < variable.min) {
                    variable.valueTooSmallMessage
                } else {
                    null
                }
            }
        }

        object Number : TextType {
            override val name: String
                get() = "number"

            override val defaultIcon: StaticIcon
                get() = StaticIcon.baseline_dialpad

            override val keyboardOptions =
                KeyboardOptions(autoCorrect = false, keyboardType = KeyboardType.Number)

            override val checker = ErrorMessageChecker { variable, value ->
                val number = value.toLongOrNull()
                if (value.isNotEmpty() && number == null) {
                    inputIllegalMessage
                } else if (variable.required && number == null) {
                    inputRequiredMessage
                } else if (number != null && number > variable.max) {
                    variable.valueTooBigMessage
                } else if (number != null && number < variable.min) {
                    variable.valueTooSmallMessage
                } else {
                    null
                }
            }
        }

        object Decimal : TextType {
            override val name: String
                get() = "decimal"

            override val defaultIcon: StaticIcon
                get() = StaticIcon.baseline_dialpad

            override val keyboardOptions =
                KeyboardOptions(autoCorrect = false, keyboardType = KeyboardType.Decimal)

            override val checker = ErrorMessageChecker { variable, value ->
                val number = value.toDoubleOrNull()
                if (value.isNotEmpty() && number == null) {
                    inputIllegalMessage
                } else if (variable.required && number == null) {
                    inputRequiredMessage
                } else if (number != null && number > variable.max) {
                    variable.valueTooBigMessage
                } else if (number != null && number < variable.min) {
                    variable.valueTooSmallMessage
                } else {
                    null
                }
            }
        }

        object Date {
            const val name: String = "date"
            val datePickerDialogProperties = DialogProperties(usePlatformDefaultWidth = false)

            val checker = ErrorMessageChecker { variable, value ->
                val epoch = variable.localizer.parseSimpleDate(value)
                if (value.isNotEmpty() && epoch == null) {
                    inputIllegalMessage
                } else if (variable.required && epoch == null) {
                    inputRequiredMessage
                } else if (epoch != null && epoch > variable.max) {
                    variable.dateTooLateMessage
                } else if (epoch != null && epoch < variable.min) {
                    variable.dateTooEarlyMessage
                } else {
                    null
                }
            }
        }

        object Switch {
            const val name: String = "switch"
            val checker = ErrorMessageChecker { variable, value ->
                val checked = value.toBooleanStrictOrNull()
                if (value.isNotEmpty() && checked == null) {
                    inputIllegalMessage
                } else if (variable.required && checked == null) {
                    inputRequiredMessage
                } else {
                    null
                }
            }
        }

        object Color {
            const val name: String = "color"
            const val COLOR_PREFIX = '#'
            val checker = ErrorMessageChecker { variable, value ->
                val color = parseColor(value)
                if (value.isNotEmpty() && color == null) {
                    inputIllegalMessage
                } else if (variable.required && color == null) {
                    inputRequiredMessage
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
                if (value.length != 7 || value[0] != COLOR_PREFIX) return null
                val hexValue = "FF" + value.substring(1)
                return hexValue.toLongOrNull(16)
            }
        }

        object Options {
            fun loadOptions(desc: String): ImmutableSet<String> = if (desc == FONTS_LIST_OPTIONS) {
                FontHandler.loadFontFamilies()
            } else {
                desc.splitIntoSet(SEPARATOR)
            }

            fun loadSelection(value: String, options: ImmutableSet<String>): ImmutableSet<String> =
                value.splitIntoSet(SEPARATOR) {
                    options.contains(it)
                }

            const val name: String = "options"
            const val SEPARATOR: Char = ','
            const val FONTS_LIST_OPTIONS: String = "fonts-list"
            val checker: ErrorMessageChecker = ErrorMessageChecker { variable, value ->
                val options = loadOptions(variable.desc)
                val selection = value.splitIntoSet(SEPARATOR)
                for (selected in selection) {
                    if (!options.contains(selected)) {
                        return@ErrorMessageChecker inputIllegalMessage
                    }
                }

                if (variable.required && selection.isEmpty()) {
                    inputRequiredMessage
                } else if (selection.size > variable.max) {
                    variable.inputSelectionCountTooHighMessage
                } else if (selection.size < variable.min) {
                    variable.inputSelectionCountTooLowMessage
                } else {
                    null
                }
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
        if (iconPath != other.iconPath) return false
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
        "Variable(key='$key', required=$required, type='$type', iconPath='$iconPath', min=$min, max=$max, prefix='$prefix', suffix='$suffix', default='$default', label_ar='$label_ar', label_fr='$label_fr', label_en='$label_en', desc_ar='$desc_ar', desc_fr='$desc_fr', desc_en='$desc_en', label='$label', desc='$desc')"

    companion object {
        const val SECTION_VARIABLE_INDEX: Int = -1
        private val resources = AbstractApplication.INSTANCE.resources
        fun key(namespace: String, name: String) = "${namespace}.$name"

        private val illegalMobileNumberMessage =
            ErrorMessage { resources.getString(R.string.illegalMobileNumber) }

        private val illegalLinePhoneNumberMessage =
            ErrorMessage { resources.getString(R.string.illegal_line_phone_number) }

        private val illegalEmailMessage =
            ErrorMessage { resources.getString(R.string.illegal_email_address) }

        private val inputRequiredMessage =
            ErrorMessage { resources.getString(R.string.input_required) }

        private val inputIllegalMessage =
            ErrorMessage { resources.getString(R.string.illegal_value) }
    }
}