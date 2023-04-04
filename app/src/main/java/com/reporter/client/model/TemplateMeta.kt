package com.reporter.client.model

import androidx.compose.runtime.Immutable
import com.google.common.collect.ImmutableMap
import com.reporter.util.model.Localizer
import com.reporter.util.model.Teller
import org.json.JSONObject

@Immutable
class TemplateMeta private constructor(
    val errors: Int,
    val variables: ImmutableMap<String, Variable> = ImmutableMap.of(),
    val records: ImmutableMap<String, Record> = ImmutableMap.of(),
) {
    fun hasErrors() = errors != 0

    private val hash by lazy { records.hashCode() + 31 * variables.hashCode() }

    override fun hashCode(): Int = hash

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TemplateMeta

        if (variables != other.variables) return false
        if (records != other.records) return false

        return true
    }

    override fun toString() = "TemplateMeta(variables=$variables, records=$records)"

    companion object {

        val empty = TemplateMeta(0, ImmutableMap.of(), ImmutableMap.of());

        fun from(json: String): TemplateMeta {
            var errors = 0
            try {
                val jsonObject = JSONObject(json)
                val variablesBuilder = ImmutableMap.builder<String, Variable>()
                try {
                    val variablesJsonArray = jsonObject.getJSONArray("variables")
                    for (i in 0 until variablesJsonArray.length()) {
                        try {
                            val variableJsonObject = variablesJsonArray.getJSONObject(i)
                            val name = variableJsonObject.getString("name")
                            variablesBuilder.put(
                                name,
                                Variable(
                                    name,
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
                            )
                        } catch (e: Exception) {
                            Teller.warn("invalid template variable#$i: $json", e)
                            errors++
                        }
                    }
                } catch (e: Exception) {
                    Teller.warn("invalid template variables: $json", e)
                    errors -= Int.MAX_VALUE
                }

                val recordsBuilder = ImmutableMap.builder<String, Record>()
                try {
                    val recordsJsonArray = jsonObject.getJSONArray("records")
                    for (i in 0 until recordsJsonArray.length()) {
                        try {
                            val recordJsonObject = recordsJsonArray.getJSONObject(i)
                            val fieldsJsonArray = recordJsonObject.getJSONArray("fields")
                            val fieldsBuilder = ImmutableMap.builder<String, Variable>()
                            for (j in 0 until fieldsJsonArray.length()) {
                                val fieldJsonObject = fieldsJsonArray.getJSONObject(j)
                                val name = fieldJsonObject.getString("name")
                                fieldsBuilder.put(
                                    name,
                                    Variable(
                                        name,
                                        fieldJsonObject.getString("type"),
                                        fieldJsonObject.optString("icon"),
                                        fieldJsonObject.optInt("min"),
                                        fieldJsonObject.getInt("max"),
                                        fieldJsonObject.optString("prefix"),
                                        fieldJsonObject.optString("suffix"),
                                        fieldJsonObject.optString("default"),
                                        fieldJsonObject.getString("label_ar"),
                                        fieldJsonObject.getString("label_fr"),
                                        fieldJsonObject.getString("label_en"),
                                        fieldJsonObject.getString("desc_ar"),
                                        fieldJsonObject.getString("desc_fr"),
                                        fieldJsonObject.getString("desc_en"),
                                    )
                                )
                            }

                            val name = recordJsonObject.getString("name")
                            recordsBuilder.put(
                                name,
                                Record(
                                    name,
                                    recordJsonObject.optString("icon"),
                                    recordJsonObject.getString("label_ar"),
                                    recordJsonObject.getString("label_fr"),
                                    recordJsonObject.getString("label_en"),
                                    recordJsonObject.getString("desc_ar"),
                                    recordJsonObject.getString("desc_fr"),
                                    recordJsonObject.getString("desc_en"),
                                    fieldsBuilder.build(),
                                )
                            )
                        } catch (e: Exception) {
                            Teller.warn("invalid template record#$i: $json", e)
                            errors++
                        }
                    }
                } catch (e: Exception) {
                    Teller.warn("invalid template records: $json", e)
                    errors -= Int.MAX_VALUE / 2
                }

                return TemplateMeta(
                    errors,
                    variablesBuilder.build(),
                    recordsBuilder.build(),
                )
            } catch (e: Exception) {
                Teller.warn("invalid template meta: $json", e)
                return TemplateMeta(-1)
            }
        }
    }
}

@Immutable
class Record internal constructor(
    val name: String,
    val icon: String,
    val label_ar: String,
    val label_fr: String,
    val label_en: String,
    val desc_ar: String,
    val desc_fr: String,
    val desc_en: String,
    val fields: ImmutableMap<String, Variable>,
) {
    val label by lazy {
        Localizer.inPrimaryLang(label_en, label_ar, label_fr)
    }

    val desc by lazy {
        Localizer.inPrimaryLang(desc_en, desc_ar, desc_fr)
    }

    private val hash: Int by lazy {
        var result = name.hashCode()
        result = 31 * result + icon.hashCode()
        result = 31 * result + label_ar.hashCode()
        result = 31 * result + label_fr.hashCode()
        result = 31 * result + label_en.hashCode()
        result = 31 * result + desc_ar.hashCode()
        result = 31 * result + desc_fr.hashCode()
        result = 31 * result + desc_en.hashCode()
        result = 31 * result + fields.hashCode()
        return@lazy result
    }

    override fun hashCode(): Int = hash

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Record

        if (name != other.name) return false
        if (icon != other.icon) return false
        if (label_ar != other.label_ar) return false
        if (label_fr != other.label_fr) return false
        if (label_en != other.label_en) return false
        if (desc_ar != other.desc_ar) return false
        if (desc_fr != other.desc_fr) return false
        if (desc_en != other.desc_en) return false
        if (fields != other.fields) return false

        return true
    }

    override fun toString() = "Record(name='$name', label='$label', fields=$fields')"

    fun debug() =
        "Record(name='$name', icon='$icon', label_ar='$label_ar', label_fr='$label_fr', label_en='$label_en', desc_ar='$desc_ar', desc_fr='$desc_fr', desc_en='$desc_en', fields=$fields, label='$label', desc='$desc')"
}

@Immutable
class Variable internal constructor(
    val name: String,
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
    val label by lazy {
        Localizer.inPrimaryLang(label_en, label_ar, label_fr)
    }

    val desc by lazy {
        Localizer.inPrimaryLang(desc_en, desc_ar, desc_fr)
    }

    private val hash by lazy {
        var result = name.hashCode()
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

        if (name != other.name) return false
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
        "Variable(name='$name', type='$type', label='$label', default='$default')"

    fun debug() =
        "Variable(name='$name', type='$type', icon='$icon', min=$min, max=$max, prefix='$prefix', suffix='$suffix', default='$default', label_ar='$label_ar', label_fr='$label_fr', label_en='$label_en', desc_ar='$desc_ar', desc_fr='$desc_fr', desc_en='$desc_en', label='$label', desc='$desc')"
}