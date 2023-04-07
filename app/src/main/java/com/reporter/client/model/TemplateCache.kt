package com.reporter.client.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableMap
import com.reporter.common.ioLaunch
import dagger.Lazy
import kotlinx.coroutines.channels.Channel

@Stable
class TemplateCache private constructor(
    val template: String,
    val variablesStates: ImmutableMap<String, VariableState>,
    val sectionStates: ImmutableList<SectionState>,
    val recordsStates: ImmutableMap<String, RecordState>,
) {

    operator fun get(key: String): MutableState<String> = variablesStates[key]!!.state

    operator fun get(variable: Variable): MutableState<String> = get(variable.key)

    fun get(namespace: String, name: String): MutableState<String> =
        get(Variable.key(namespace, name))

    fun setter(key: String): (String) -> Unit = variablesStates[key]!!.setter

    fun setter(variable: Variable): (String) -> Unit = setter(variable.key)

    fun setter(namespace: String, name: String): (String) -> Unit =
        setter(Variable.key(namespace, name))

    operator fun set(key: String, newContent: String?) {
        val variableState = variablesStates[key]
        if (variableState != null) {
            setState(variableState.variable, variableState.state, newContent)
        }
    }

    operator fun set(variableState: VariableState, newContent: String?) {
        setState(variableState.variable, variableState.state, newContent)
    }

    override fun equals(other: Any?) =
        this === other || (other is TemplateCache && other.template == this.template)

    override fun hashCode() = template.hashCode()

    override fun toString() = "TemplateCache(template='$template')"

    companion object {

        @Volatile
        private var globalValueDAO: ValueDAO? = null
        private val pendingUpdates = Channel<ValueUpdate>(capacity = Channel.UNLIMITED)

        init {
            ioLaunch {
                val firstUpdate = pendingUpdates.receive()
                val dao = globalValueDAO!!
                dao.execute(firstUpdate)
                while (true) {
                    dao.execute(pendingUpdates.receive())
                }
            }
        }

        suspend fun from(
            templateName: String,
            meta: TemplateMeta,
            valueDAO: Lazy<ValueDAO>
        ): TemplateCache {
            val variablesBuilder: ImmutableMap.Builder<String, VariableState> =
                ImmutableMap.builder()
            val sectionsBuilder: ImmutableList.Builder<SectionState> = ImmutableList.builder()
            val recordsBuilder: ImmutableMap.Builder<String, RecordState> = ImmutableMap.builder()
            buildEmptyCache(meta, variablesBuilder, sectionsBuilder, recordsBuilder)
            val variablesStates = variablesBuilder.build()

            val dao: ValueDAO = valueDAO.get()
            val loadedValues: List<Value> = dao.findValuesPrefixedBy(templateName)
            val toDelete: ArrayList<Value> = ArrayList(loadedValues.size)

            for (loadedValue in loadedValues) {
                val variableState = variablesStates[loadedValue.key]
                if (variableState != null) {
                    variableState.state.value = loadedValue.content
                } else {
                    toDelete.add(loadedValue)
                }
            }

            dao.deleteAll(toDelete)
            globalValueDAO = dao

            return TemplateCache(
                templateName,
                variablesStates,
                sectionsBuilder.build(),
                recordsBuilder.build()
            )
        }

        private fun buildEmptyCache(
            meta: TemplateMeta,
            variablesBuilder: ImmutableMap.Builder<String, VariableState>,
            sectionsBuilder: ImmutableList.Builder<SectionState>,
            recordsBuilder: ImmutableMap.Builder<String, RecordState>,
        ) {
            val declaredSections: ImmutableList<Section> = meta.sections
            val declaredRecords: ImmutableMap<String, Record> = meta.records

            for (section in declaredSections) {
                val varsBuilder: ImmutableMap.Builder<String, VariableState> =
                    ImmutableMap.builder()
                for (variable in section.variables.values) {
                    varsBuilder.put(variable.key, addVariableState(variablesBuilder, variable))
                }
                sectionsBuilder.add(SectionState(section, varsBuilder.build()))
            }

            for (record in declaredRecords.values) {
                val varsBuilder: ImmutableMap.Builder<String, VariableState> =
                    ImmutableMap.builder()
                for (variable in record.variables.values) {
                    varsBuilder.put(variable.key, addVariableState(variablesBuilder, variable))
                }
                recordsBuilder.put(record.name, RecordState(record, varsBuilder.build()))
            }
        }

        private fun addVariableState(
            builder: ImmutableMap.Builder<String, VariableState>,
            variable: Variable
        ): VariableState {
            val state: MutableState<String> = mutableStateOf(variable.default)
            val variableState = VariableState(variable, state) {
                setState(variable, state, it)
            }
            builder.put(variable.key, variableState)
            return variableState
        }

        private fun setState(
            variable: Variable,
            state: MutableState<String>,
            newContent: String?,
        ) {
            val currentContent = state.value
            if (newContent != currentContent) {
                val namespace = variable.namespace
                val name = variable.name
                if (newContent != null) {
                    val newValue = Value(
                        namespace = namespace,
                        name = name,
                        lastUpdate = System.currentTimeMillis(),
                        content = newContent,
                    )
                    pendingUpdates.trySend(ValueUpdate(namespace, name, newValue))
                    state.value = newContent
                } else {
                    pendingUpdates.trySend(ValueUpdate(namespace, name, null))
                    state.value = variable.default
                }
            }
        }
    }
}

class ValueUpdate(
    val namespace: String,
    val name: String,
    val newValue: Value?,
) {
    override fun toString(): String {
        return "ValueUpdate(namespace='$namespace', name='$name', newValue=$newValue)"
    }
}

class VariableState(
    val variable: Variable,
    val state: MutableState<String>,
    val setter: (String) -> Unit,
) {
    override fun toString(): String {
        return "VariableState(variable=$variable)"
    }
}

class RecordState(
    val record: Record,
    val variables: ImmutableMap<String, VariableState>,
) {
    override fun toString(): String {
        return "RecordState(record=$record)"
    }
}

class SectionState(
    val section: Section,
    val variables: ImmutableMap<String, VariableState>,
) {
    override fun toString(): String {
        return "SectionState(section=$section)"
    }
}