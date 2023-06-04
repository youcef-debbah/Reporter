package dz.nexatech.reporter.client.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableMap
import dz.nexatech.reporter.client.common.withIO
import dz.nexatech.reporter.client.common.withMain
import dz.nexatech.reporter.client.core.AbstractValue
import dz.nexatech.reporter.client.core.ValueUpdate
import dz.nexatech.reporter.client.ui.InputHandler
import dz.nexatech.reporter.util.model.Teller
import dz.nexatech.reporter.util.model.errorHtmlPage
import io.pebbletemplates.pebble.template.PebbleTemplate
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import java.io.StringWriter

@Stable
class TemplateState private constructor(
    val template: String,
    val variablesStates: ImmutableMap<String, VariableState>,
    val sectionStates: ImmutableList<SectionState>,
    val recordsStates: ImmutableMap<String, RecordState>,
    val environment: ImmutableMap<String, Any>,
    val lastUpdate: MutableSharedFlow<ValueUpdate>,
    val fontsVariablesStates: ImmutableMap<String, VariableState>,
) {
    operator fun get(key: String): MutableState<String> = variablesStates[key]!!.state

    operator fun get(variable: Variable): MutableState<String> = get(variable.key)

    fun get(namespace: String, name: String): MutableState<String> =
        get(Variable.key(namespace, name))

    fun setter(key: String): (String) -> Unit = variablesStates[key]!!.setter

    fun setter(variable: Variable): (String) -> Unit = setter(variable.key)

    fun setter(namespace: String, name: String): (String) -> Unit =
        setter(Variable.key(namespace, name))

    operator fun set(key: String, newContent: String) {
        val variableState = variablesStates[key]
        if (variableState != null) {
            setState(variableState.variable, variableState.state, newContent, lastUpdate)
        }
    }

    operator fun set(variableState: VariableState, newContent: String) {
        setState(variableState.variable, variableState.state, newContent, lastUpdate)
    }

    override fun equals(other: Any?) =
        this === other || (other is TemplateState && other.template == this.template)

    override fun hashCode() = template.hashCode()

    override fun toString() = "TemplateState(template='$template')"

    companion object {

        suspend fun from(
            meta: TemplateMeta,
            inputRepository: InputRepository,
        ): TemplateState {
            val templateName = meta.template
            val environmentBuilder: ImmutableMap.Builder<String, Any> = ImmutableMap.builder()
            val variablesBuilder: ImmutableMap.Builder<String, VariableState> =
                ImmutableMap.builder()
            val fontVariablesBuilder: ImmutableMap.Builder<String, VariableState> =
                ImmutableMap.builder()
            val sectionsBuilder: ImmutableList.Builder<SectionState> = ImmutableList.builder()
            val recordsBuilder: ImmutableMap.Builder<String, RecordState> = ImmutableMap.builder()
            val lastUpdate = MutableSharedFlow<ValueUpdate>(
                replay = 1,
                extraBufferCapacity = 0,
                onBufferOverflow = BufferOverflow.DROP_OLDEST
            )
            lastUpdate.tryEmit(ValueUpdate(templateName, "", null))

            buildEmptyState(
                meta,
                variablesBuilder,
                fontVariablesBuilder,
                sectionsBuilder,
                recordsBuilder,
                environmentBuilder,
                lastUpdate
            )
            val variablesStates = variablesBuilder.build()

            withIO {
                val loadedValues: List<AbstractValue> = inputRepository.findValuesByNamespacePrefix(templateName)
                for (loadedValue in loadedValues) {
                    val variableState = variablesStates[loadedValue.key]
                    if (variableState != null) {
                        variableState.state.value = loadedValue.content
                    } else {
                        inputRepository.delete(loadedValue.namespace, loadedValue.name)
                    }
                }
            }

            return TemplateState(
                templateName,
                variablesStates,
                sectionsBuilder.build(),
                recordsBuilder.build(),
                environmentBuilder.build(),
                lastUpdate,
                fontVariablesBuilder.build()
            )
        }

        private suspend fun buildEmptyState(
            meta: TemplateMeta,
            variablesBuilder: ImmutableMap.Builder<String, VariableState>,
            fontVariablesBuilder: ImmutableMap.Builder<String, VariableState>,
            sectionsBuilder: ImmutableList.Builder<SectionState>,
            recordsBuilder: ImmutableMap.Builder<String, RecordState>,
            environmentBuilder: ImmutableMap.Builder<String, Any>,
            lastUpdate: MutableSharedFlow<ValueUpdate>,
        ) {
            val declaredSections: ImmutableList<Section> = meta.sections
            val declaredRecords: ImmutableMap<String, Record> = meta.records

            for (section in declaredSections) {
                val varsBuilder: ImmutableMap.Builder<String, VariableState> =
                    ImmutableMap.builder()
                for (variable in section.variables.values) {
                    val variableState = addVariableState(
                        variablesBuilder,
                        fontVariablesBuilder,
                        variable,
                        lastUpdate
                    )
                    varsBuilder.put(variable.key, variableState)
                    environmentBuilder.put(variable.name, variableState)
                }

                val variableStates = varsBuilder.build()
                sectionsBuilder.add(
                    SectionState(
                        section,
                        variableStates,
                        budgetTextState(variableStates.values)
                    )
                )
            }

            for (record in declaredRecords.values) {
                val varsBuilder: ImmutableMap.Builder<String, VariableState> =
                    ImmutableMap.builder()
                val recordEnvironment = ImmutableMap.builder<String, VariableState>()

                for (variable in record.variables.values) {
                    val variableState = addVariableState(
                        variablesBuilder,
                        fontVariablesBuilder,
                        variable,
                        lastUpdate
                    )
                    varsBuilder.put(variable.key, variableState)
                    recordEnvironment.put(variable.name, variableState)
                }

                val variableStates = varsBuilder.build()
                recordsBuilder.put(
                    record.name,
                    RecordState(record, variableStates, budgetTextState(variableStates.values))
                )
                environmentBuilder.put(record.name, recordEnvironment)
            }
        }

        private suspend fun budgetTextState(variableStates: Collection<VariableState>): State<String> =
            withMain {
                derivedStateOf {
                    var errorsCount = 0
                    for (variableState in variableStates) {
                        if (variableState.variable.errorMessage(variableState.state.value) != null) {
                            errorsCount++
                        }
                    }
                    if (errorsCount > 0) errorsCount.toString() else ""
                }
            }

        private fun addVariableState(
            variablesBuilder: ImmutableMap.Builder<String, VariableState>,
            fontVariablesBuilder: ImmutableMap.Builder<String, VariableState>,
            variable: Variable,
            lastUpdate: MutableSharedFlow<ValueUpdate>,
        ): VariableState {
            val state: MutableState<String> = mutableStateOf(variable.default)
            val variableState = VariableState(variable, state) {
                setState(variable, state, it, lastUpdate)
            }
            variablesBuilder.put(variable.key, variableState)
            if (variable.type == Variable.Type.Font.name) {
                fontVariablesBuilder.put(variable.key, variableState)
            }
            return variableState
        }

        private fun setState(
            variable: Variable,
            state: MutableState<String>,
            newContent: String,
            lastUpdate: MutableSharedFlow<ValueUpdate>,
        ) {
            val currentContent = state.value
            if (newContent != currentContent) {
                val namespace = variable.namespace
                val name = variable.name
                val update = ValueUpdate(namespace, name, newContent)

                state.value = newContent
                InputHandler.execute(update)
                lastUpdate.tryEmit(update)
            }
        }
    }
}

@Stable
class VariableState(
    val variable: Variable,
    val state: MutableState<String>,
    val setter: (String) -> Unit,
) {
    override fun toString(): String {
        return state.value
    }
}

@Stable
class RecordState(
    val record: Record,
    val variables: ImmutableMap<String, VariableState>,
    val badgeText: State<String>,
) {
    override fun toString(): String {
        return "RecordState(record=$record)"
    }
}

@Stable
class SectionState(
    val section: Section,
    val variables: ImmutableMap<String, VariableState>,
    val badgeText: State<String>,
) {
    override fun toString(): String {
        return "SectionState(section=$section)"
    }
}

fun PebbleTemplate.evaluateState(
    templateState: TemplateState,
): String = try {
    val writer = StringWriter()
    evaluate(writer, templateState.environment)
    writer.toString()
} catch (e: Exception) {
    Teller.error("error while evaluating template: ${templateState.template}", e)
    errorHtmlPage(e.message ?: "something went wrong while evaluating this HTML content!")
}