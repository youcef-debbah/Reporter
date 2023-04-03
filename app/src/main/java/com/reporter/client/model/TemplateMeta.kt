package com.reporter.client.model

import androidx.compose.runtime.Immutable
import com.google.common.collect.ImmutableList
import com.reporter.util.model.Localizer
import com.reporter.util.model.Teller
import org.json.JSONException
import org.json.JSONObject

@Immutable
class TemplateMeta private constructor(
    val errors: Int,
    val variables: ImmutableList<Variable> = ImmutableList.of(),
    val records: ImmutableList<Record> = ImmutableList.of(),
) {
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

    override fun toString(): String {
        return "TemplateMeta(variables=$variables, records=$records)"
    }

    companion object {

        val empty = TemplateMeta(0, ImmutableList.of(), ImmutableList.of());

        fun from(json: String): TemplateMeta {
            var errors = 0
            try {
                val jsonObject = JSONObject(json)
                val variablesBuilder = ImmutableList.builder<Variable>()
                try {
                    val variablesJsonArray = jsonObject.getJSONArray("variables")
                    for (i in 0 until variablesJsonArray.length()) {
                        try {
                            val variableJsonObject = variablesJsonArray.getJSONObject(i)
                            variablesBuilder.add(
                                Variable(
                                    variableJsonObject.getString("name"),
                                    variableJsonObject.getString("type"),
                                    variableJsonObject.getString("icon"),
                                    variableJsonObject.getInt("min"),
                                    variableJsonObject.getInt("max"),
                                    variableJsonObject.getString("prefix"),
                                    variableJsonObject.getString("suffix"),
                                    variableJsonObject.getString("default"),
                                    variableJsonObject.getString("label_ar"),
                                    variableJsonObject.getString("label_fr"),
                                    variableJsonObject.getString("label_en"),
                                    variableJsonObject.getString("desc_ar"),
                                    variableJsonObject.getString("desc_fr"),
                                    variableJsonObject.getString("desc_en"),
                                )
                            )
                        } catch (e: JSONException) {
                            Teller.warn("invalid template variable#$i: $json", e)
                            errors++
                        }
                    }
                } catch (e: JSONException) {
                    Teller.warn("invalid template variables: $json", e)
                    errors -= Int.MAX_VALUE
                }

                val recordsBuilder = ImmutableList.builder<Record>()
                try {
                    val recordsJsonArray = jsonObject.getJSONArray("records")
                    for (i in 0 until recordsJsonArray.length()) {
                        try {
                            val recordJsonObject = recordsJsonArray.getJSONObject(i)
                            val fieldsJsonArray = recordJsonObject.getJSONArray("fields")
                            val fieldsBuilder = ImmutableList.builder<Variable>()
                            for (j in 0 until fieldsJsonArray.length()) {
                                val fieldJsonObject = fieldsJsonArray.getJSONObject(j)
                                fieldsBuilder.add(
                                    Variable(
                                        fieldJsonObject.getString("name"),
                                        fieldJsonObject.getString("type"),
                                        fieldJsonObject.getString("icon"),
                                        fieldJsonObject.getInt("min"),
                                        fieldJsonObject.getInt("max"),
                                        fieldJsonObject.getString("prefix"),
                                        fieldJsonObject.getString("suffix"),
                                        fieldJsonObject.getString("default"),
                                        fieldJsonObject.getString("label_ar"),
                                        fieldJsonObject.getString("label_fr"),
                                        fieldJsonObject.getString("label_en"),
                                        fieldJsonObject.getString("desc_ar"),
                                        fieldJsonObject.getString("desc_fr"),
                                        fieldJsonObject.getString("desc_en"),
                                    )
                                )
                            }

                            recordsBuilder.add(
                                Record(
                                    recordJsonObject.getString("name"),
                                    recordJsonObject.getString("icon"),
                                    recordJsonObject.getString("label_ar"),
                                    recordJsonObject.getString("label_fr"),
                                    recordJsonObject.getString("label_en"),
                                    recordJsonObject.getString("desc_ar"),
                                    recordJsonObject.getString("desc_fr"),
                                    recordJsonObject.getString("desc_en"),
                                    fieldsBuilder.build(),
                                )
                            )
                        } catch (e: JSONException) {
                            Teller.warn("invalid template record#$i: $json", e)
                            errors++
                        }
                    }
                } catch (e: JSONException) {
                    Teller.warn("invalid template records: $json", e)
                    errors -= Int.MAX_VALUE / 2
                }

                return TemplateMeta(
                    errors,
                    variablesBuilder.build(),
                    recordsBuilder.build(),
                )
            } catch (e: JSONException) {
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
    val fields: ImmutableList<Variable>,
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

    override fun toString(): String {
        return "Record(name='$name', label='$label', desc='$desc, fields=$fields')"
    }
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

    override fun toString(): String {
        return "Variable(name='$name', type='$type', max=$max, prefix='$prefix', suffix='$suffix', default='$default', label='$label', desc='$desc')"
    }
}