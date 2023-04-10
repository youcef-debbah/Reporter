package com.reporter.client.model

import androidx.compose.runtime.Immutable
import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableMap
import com.reporter.common.IntCounter
import com.reporter.util.model.Localizer
import com.reporter.util.model.Teller
import org.json.JSONObject

@Immutable
class TemplateMeta private constructor(
    val template: String,
    val errorCode: Int,
    val sectionsVariables: ImmutableMap<String, Variable> = ImmutableMap.of(),
    val sections: ImmutableList<Section> = ImmutableList.of(),
    val recordsVariables: ImmutableMap<String, Variable> = ImmutableMap.of(),
    val records: ImmutableMap<String, Record> = ImmutableMap.of(),
) {
    fun hasErrors() = errorCode != 0

    private val hash by lazy {
        var result = template.hashCode()
        result = 31 * result + sections.hashCode()
        result = 31 * result + records.hashCode()
        return@lazy result
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
        fun from(templateName: String, json: String): TemplateMeta {
            if (json.isEmpty())
                return TemplateMeta(template = templateName, errorCode = 404)

            try {
                val jsonObject = JSONObject(json)
                val errorCode = IntCounter()
                val sections = buildSections(templateName, json, jsonObject, errorCode)
                val records = buildRecords(templateName, json, jsonObject, errorCode)
                return TemplateMeta(
                    templateName,
                    errorCode.value,
                    sections.first,
                    sections.second,
                    records.first,
                    records.second,
                )
            } catch (e: Exception) {
                Teller.warn("invalid template meta for '$templateName': $json", e)
                return TemplateMeta(template = templateName, errorCode = 400)
            }
        }

        private fun buildSections(
            templateName: String,
            json: String,
            jsonObject: JSONObject,
            errorCode: IntCounter,
        ): Pair<ImmutableMap<String, Variable>, ImmutableList<Section>> {
            try {
                val sectionsJsonArray = jsonObject.getJSONArray("sections")
                val sectionsBuilder = ImmutableList.builder<Section>()
                val sectionsVariablesBuilder = ImmutableMap.builder<String, Variable>()
                for (i in 0 until sectionsJsonArray.length()) {
                    try {
                        val sectionJsonObject = sectionsJsonArray.getJSONObject(i)
                        val variablesJsonArray = sectionJsonObject.getJSONArray("variables")
                        val variablesBuilder = ImmutableMap.builder<String, Variable>()

                        for (j in 0 until variablesJsonArray.length()) {
                            val variableJsonObject = variablesJsonArray.getJSONObject(j)
                            val name = variableJsonObject.getString("name")
                            val variable = Variable(
                                templateName,
                                name,
                                variableJsonObject.optBoolean("required"),
                                variableJsonObject.getString("type"),
                                variableJsonObject.optString("icon"),
                                variableJsonObject.optInt("min"),
                                variableJsonObject.getInt("max"),
                                variableJsonObject.optString("prefix"),
                                variableJsonObject.optString("suffix"),
                                variableJsonObject.optString("default"),
                                variableJsonObject.getString("label_ar"),
                                variableJsonObject.getString("label_fr"),
                                variableJsonObject.getString("label_en"),
                                variableJsonObject.getString("desc_ar"),
                                variableJsonObject.getString("desc_fr"),
                                variableJsonObject.getString("desc_en"),
                            )

                            variablesBuilder.put(name, variable)
                            sectionsVariablesBuilder.put(name, variable)
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
                            )
                        )
                    } catch (e: Exception) {
                        Teller.warn("invalid template section#$i for '$templateName': $json", e)
                        errorCode.dec()
                        errorCode.addFlag(1)
                    }
                }
                return Pair(sectionsVariablesBuilder.build(), sectionsBuilder.build())
            } catch (e: Exception) {
                Teller.warn("invalid template sections for '$templateName': $json", e)
                errorCode.addFlag(1)
                return Pair(ImmutableMap.of(), ImmutableList.of())
            }
        }

        private fun buildRecords(
            templateName: String,
            json: String,
            jsonObject: JSONObject,
            errorCode: IntCounter,
        ): Pair<ImmutableMap<String, Variable>, ImmutableMap<String, Record>> {
            try {
                val recordsJsonArray = jsonObject.optJSONArray("records")
                if (recordsJsonArray != null) {
                    val recordsVariablesBuilder = ImmutableMap.builder<String, Variable>()
                    val recordsBuilder = ImmutableMap.builder<String, Record>()
                    for (i in 0 until recordsJsonArray.length()) {
                        try {
                            val recordJsonObject = recordsJsonArray.getJSONObject(i)
                            val variablesJsonArray = recordJsonObject.getJSONArray("variables")
                            val variablesBuilder = ImmutableMap.builder<String, Variable>()

                            val recordName = recordJsonObject.getString("name")
                            val recordNamespace = Record.namespace(templateName, recordName)

                            for (j in 0 until variablesJsonArray.length()) {
                                val variableJsonObject = variablesJsonArray.getJSONObject(j)
                                val name = variableJsonObject.getString("name")
                                val variable = Variable(
                                    recordNamespace,
                                    name,
                                    variableJsonObject.optBoolean("required"),
                                    variableJsonObject.getString("type"),
                                    variableJsonObject.optString("icon"),
                                    variableJsonObject.optInt("min"),
                                    variableJsonObject.getInt("max"),
                                    variableJsonObject.optString("prefix"),
                                    variableJsonObject.optString("suffix"),
                                    variableJsonObject.optString("default"),
                                    variableJsonObject.getString("label_ar"),
                                    variableJsonObject.getString("label_fr"),
                                    variableJsonObject.getString("label_en"),
                                    variableJsonObject.getString("desc_ar"),
                                    variableJsonObject.getString("desc_fr"),
                                    variableJsonObject.getString("desc_en"),
                                )
                                variablesBuilder.put(name, variable)
                                recordsVariablesBuilder.put("$recordName.$name", variable)
                            }

                            recordsBuilder.put(
                                recordNamespace,
                                Record(
                                    recordNamespace,
                                    recordName,
                                    recordJsonObject.optString("icon"),
                                    recordJsonObject.getString("label_ar"),
                                    recordJsonObject.getString("label_fr"),
                                    recordJsonObject.getString("label_en"),
                                    recordJsonObject.getString("desc_ar"),
                                    recordJsonObject.getString("desc_fr"),
                                    recordJsonObject.getString("desc_en"),
                                    variablesBuilder.build(),
                                )
                            )
                        } catch (e: Exception) {
                            Teller.warn("invalid template record#$i for '$templateName': $json", e)
                            errorCode.dec()
                            errorCode.addFlag(2)
                        }
                    }
                    return Pair(recordsVariablesBuilder.build(), recordsBuilder.build())
                }
            } catch (e: Exception) {
                Teller.warn("invalid template records for '$templateName': $json", e)
                errorCode.addFlag(2)
            }

            return Pair(ImmutableMap.of(), ImmutableMap.of())
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
    val variables: ImmutableMap<String, Variable>,
) {

    val className: String = this.javaClass.simpleName

    val label by lazy {
        Localizer.inPrimaryLang(label_en, label_ar, label_fr)
    }

    val desc by lazy {
        Localizer.inPrimaryLang(desc_en, desc_ar, desc_fr)
    }

    private val hash: Int by lazy {
        var result = namespace.hashCode()
        result = 31 * result + icon.hashCode()
        result = 31 * result + label_ar.hashCode()
        result = 31 * result + label_fr.hashCode()
        result = 31 * result + label_en.hashCode()
        result = 31 * result + desc_ar.hashCode()
        result = 31 * result + desc_fr.hashCode()
        result = 31 * result + desc_en.hashCode()
        result = 31 * result + variables.hashCode()
        return@lazy result
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
    namespace: String,
    val name: String,
    icon: String,
    label_ar: String,
    label_fr: String,
    label_en: String,
    desc_ar: String,
    desc_fr: String,
    desc_en: String,
    variables: ImmutableMap<String, Variable>,
) : Form(namespace, icon, label_ar, label_fr, label_en, desc_ar, desc_fr, desc_en, variables) {
    companion object {
        fun namespace(template: String, recordName: String) = "$template@$recordName"
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
    variables: ImmutableMap<String, Variable>,
) : Form(namespace, icon, label_ar, label_fr, label_en, desc_ar, desc_fr, desc_en, variables)

@Immutable
class Variable internal constructor(
    val namespace: String,
    val name: String,
    val required: Boolean,
    val type: String,
    val icon: String,
    val min: Int,
    val max: Int,
    val prefix: String,
    val suffix: String,
    val default: String,
    val label_ar: String,
    val label_fr: String,
    val label_en: String,
    val desc_ar: String,
    val desc_fr: String,
    val desc_en: String,
) {
    val key = key(namespace, name)

    val label by lazy {
        Localizer.inPrimaryLang(label_en, label_ar, label_fr)
    }

    val desc by lazy {
        Localizer.inPrimaryLang(desc_en, desc_ar, desc_fr)
    }

    private val hash by lazy {
        var result = key.hashCode()
        result = 31 * result + required.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + icon.hashCode()
        result = 31 * result + min
        result = 31 * result + max
        result = 31 * result + prefix.hashCode()
        result = 31 * result + suffix.hashCode()
        result = 31 * result + default.hashCode()
        result = 31 * result + label_ar.hashCode()
        result = 31 * result + label_fr.hashCode()
        result = 31 * result + label_en.hashCode()
        result = 31 * result + desc_ar.hashCode()
        result = 31 * result + desc_fr.hashCode()
        result = 31 * result + desc_en.hashCode()
        return@lazy result
    }

    override fun hashCode() = hash

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Variable

        if (key != other.key) return false
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

    companion object {
        fun key(namespace: String, name: String) = "${namespace}.$name"
    }
}