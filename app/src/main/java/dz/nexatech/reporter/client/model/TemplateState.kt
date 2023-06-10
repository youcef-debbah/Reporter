package dz.nexatech.reporter.client.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableMap
import dagger.hilt.android.internal.ThreadUtil
import dz.nexatech.reporter.client.common.addHash
import dz.nexatech.reporter.client.common.ioLaunch
import dz.nexatech.reporter.client.common.withIO
import dz.nexatech.reporter.client.common.withMain
import dz.nexatech.reporter.client.core.AbstractValue
import dz.nexatech.reporter.client.core.ValueOperation
import dz.nexatech.reporter.client.ui.InputHandler
import dz.nexatech.reporter.util.model.Teller
import dz.nexatech.reporter.util.model.errorHtmlPage
import io.pebbletemplates.pebble.template.PebbleTemplate
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import java.io.StringWriter
import java.util.SortedMap
import java.util.TreeMap

@Stable
class TemplateState private constructor(
    val templateName: String,
    val sectionsVariableStates: ImmutableMap<String, VariableState>,
    val sectionStates: ImmutableList<SectionState>,
    val recordsStates: ImmutableList<RecordState>,
    val lastUpdate: MutableSharedFlow<String>,
    val fontsVariablesStates: ImmutableList<VariableState>,
    private val inputRepository: InputRepository,
) {

    override fun toString() = "TemplateState(template='$templateName')" + hashCode().toString(16)

    fun currentEnvironment(): ImmutableMap<String, Any> =
        ImmutableMap.Builder<String, Any>().apply {
            putAll(sectionsVariableStates)
            for (tuplesState in recordsStates) {
                put(tuplesState.record.name, tuplesState.tuples.toList())
            }
        }.build()

    fun reloadTuples(record: RecordState) {
        ioLaunch {
            updateRecordsStates(inputRepository, templateName, ImmutableList.of(record), lastUpdate)
        }
    }

    fun createTuple(recordState: RecordState) {
        ThreadUtil.ensureMainThread()
        val index = recordState.maxIndex.value + 1

        val tupleBuilder = ImmutableMap.builder<String, VariableState>()
        for (variable in recordState.record.variables) {
            tupleBuilder.put(
                variable.name,
                createVariableState(
                    variable = variable,
                    lastUpdate = lastUpdate,
                    index = index,
                    value = variable.default,
                ),
            )
            InputHandler.execute(
                ValueOperation.Update(
                    namespace = variable.namespace,
                    index = index,
                    name = variable.name,
                    newContent = variable.default
                )
            )
        }

        recordState.maxIndex.value = index
        recordState.tuples.add(tupleBuilder.build())
    }

    fun deleteTuple(recordState: RecordState, target: ImmutableMap<String, VariableState>) {
        if (recordState.tuples.remove(target)) {
            for (variableState in target.values) {
                val variable = variableState.variable
                InputHandler.execute(
                    ValueOperation.Delete(
                        variable.namespace,
                        variableState.index,
                        variable.name
                    )
                )
            }
        }
    }

    companion object {

        suspend fun from(
            meta: TemplateMeta,
            inputRepository: InputRepository,
        ): TemplateState {
            val templateName = meta.template
            val sectionsVariableStatesBuilder: ImmutableMap.Builder<String, VariableState> =
                ImmutableMap.builder()
            val fontVariablesBuilder: ImmutableList.Builder<VariableState> = ImmutableList.builder()
            val sectionsBuilder: ImmutableList.Builder<SectionState> = ImmutableList.builder()
            val recordsBuilder: ImmutableList.Builder<RecordState> = ImmutableList.builder()
            val lastUpdate = MutableSharedFlow<String>(
                replay = 1,
                extraBufferCapacity = 0,
                onBufferOverflow = BufferOverflow.DROP_OLDEST
            )
            lastUpdate.tryEmit("")

            buildEmptyState(
                meta = meta,
                allSectionsVariablesBuilder = sectionsVariableStatesBuilder,
                fontVariablesBuilder = fontVariablesBuilder,
                sectionsBuilder = sectionsBuilder,
                recordsBuilder = recordsBuilder,
                lastUpdate = lastUpdate
            )

            val sectionsVariableStates = sectionsVariableStatesBuilder.build()
            val recordsStates = recordsBuilder.build()
            withIO {
                updateSectionsStates(inputRepository, templateName, sectionsVariableStates)
                updateRecordsStates(inputRepository, templateName, recordsStates, lastUpdate)
            }

            return TemplateState(
                templateName = templateName,
                sectionsVariableStates = sectionsVariableStates,
                sectionStates = sectionsBuilder.build(),
                recordsStates = recordsStates,
                lastUpdate = lastUpdate,
                fontsVariablesStates = fontVariablesBuilder.build(),
                inputRepository = inputRepository,
            )
        }

        private suspend fun updateRecordsStates(
            inputRepository: InputRepository,
            templateName: String,
            recordsStates: ImmutableList<RecordState>,
            lastUpdate: MutableSharedFlow<String>,
        ) {
            val loadedValues = inputRepository.loadRecordsVariablesValues(templateName)
            if (loadedValues.isEmpty()) {
                for (recordTuples in recordsStates) {
                    recordTuples.tuples.clear()
                }
            } else {
                val loadedTuples = TreeMap<Int, MutableMap<String, AbstractValue>>()
                for (loadedValue in loadedValues) {
                    loadedTuples.compute(loadedValue.index) { _, values ->
                        values ?: mutableMapOf<String, AbstractValue>().apply {
                            put(loadedValue.key, loadedValue)
                        }
                    }
                }

                for (recordState in recordsStates) {
                    updateRecordTuples(
                        loadedTuples = loadedTuples,
                        record = recordState.record,
                        tuples = recordState.tuples,
                        maxIndexState = recordState.maxIndex,
                        lastUpdate = lastUpdate,
                    )
                }

                // queue unused values to be deleted from the database
                for (loadedTuple in loadedTuples.values) {
                    for (value in loadedTuple.values) {
                        inputRepository.execute(
                            ValueOperation.Delete(
                                namespace = value.namespace,
                                index = value.index,
                                name = value.name,
                            )
                        )
                    }
                }
            }
        }

        private suspend fun updateRecordTuples(
            loadedTuples: SortedMap<Int, MutableMap<String, AbstractValue>>,
            record: Record,
            tuples: SnapshotStateList<ImmutableMap<String, VariableState>>,
            maxIndexState: MutableState<Int>,
            lastUpdate: MutableSharedFlow<String>,
        ) {
            val draft = ArrayList<ImmutableMap<String, VariableState>>(loadedTuples.size)
            var maxIndex = -1
            for (loadedEntry in loadedTuples.entries) {
                val index = loadedEntry.key
                if (index > maxIndex) {
                    maxIndex = index
                }
                val loadedTuple = loadedEntry.value
                val builder = ImmutableMap.Builder<String, VariableState>()
                for (variable in record.variables) {
                    val value: AbstractValue? = loadedTuple.remove(variable.key)
                    if (value == null) {
                        InputHandler.execute(
                            ValueOperation.Update(
                                namespace = variable.namespace,
                                index = index,
                                name = variable.name,
                                newContent = variable.default
                            )
                        )
                    }
                    builder.put(
                        variable.name,
                        createVariableState(
                            variable = variable,
                            lastUpdate = lastUpdate,
                            index = index,
                            value = value?.content ?: variable.default,
                        ),
                    )
                }
                draft.add(builder.build())
            }

            // update the UI
            withMain {
                maxIndexState.value = maxIndex
                tuples.clear()
                tuples.addAll(draft)
            }
        }

        private suspend fun updateSectionsStates(
            inputRepository: InputRepository,
            templateName: String,
            sectionsStates: ImmutableMap<String, VariableState>,
        ) {
            val variablesValues: List<AbstractValue> =
                inputRepository.loadSectionVariablesValues(templateName)
            for (variableValue in variablesValues) {
                val variableState = sectionsStates[variableValue.name]
                if (variableState != null) {
                    variableState.state.value = variableValue.content
                } else {
                    inputRepository.execute(
                        ValueOperation.Delete(
                            variableValue.namespace,
                            variableValue.index,
                            variableValue.name,
                        )
                    )
                }
            }
        }

        private suspend fun buildEmptyState(
            meta: TemplateMeta,
            allSectionsVariablesBuilder: ImmutableMap.Builder<String, VariableState>,
            fontVariablesBuilder: ImmutableList.Builder<VariableState>,
            sectionsBuilder: ImmutableList.Builder<SectionState>,
            recordsBuilder: ImmutableList.Builder<RecordState>,
            lastUpdate: MutableSharedFlow<String>,
        ) {
            val declaredSections: ImmutableList<Section> = meta.sections
            val declaredRecords: ImmutableMap<String, Record> = meta.records

            for (section in declaredSections) {
                val currentSectionVariablesBuilder: ImmutableList.Builder<VariableState> =
                    ImmutableList.builder()
                for (variable in section.variables) {
                    val variableState = createVariableState(
                        variable,
                        lastUpdate,
                        Variable.SECTION_VARIABLE_INDEX,
                    )
                    allSectionsVariablesBuilder.put(variable.name, variableState)
                    currentSectionVariablesBuilder.add(variableState)
                    if (variable.type == Variable.Type.Font.name) {
                        fontVariablesBuilder.add(variableState)
                    }
                }

                val sectionVariables = currentSectionVariablesBuilder.build()
                sectionsBuilder.add(
                    SectionState(
                        section,
                        sectionVariables,
                        sectionBudgetTextState(sectionVariables)
                    )
                )
            }

            for (record in declaredRecords.values) {
                val tuples = SnapshotStateList<ImmutableMap<String, VariableState>>()
                val maxIndex = mutableStateOf(-1)
                recordsBuilder.add(
                    RecordState(
                        record,
                        tuples,
                        maxIndex,
                        recordBudgetTextState(tuples)
                    )
                )
            }
        }

        private suspend fun recordBudgetTextState(tuples: SnapshotStateList<ImmutableMap<String, VariableState>>): State<String> =
            withMain {
                derivedStateOf {
                    var errorsCount = 0
                    val iterator = tuples.iterator()
                    while (iterator.hasNext()) {
                        val variableStates = iterator.next().values
                        for (variableState in variableStates) {
                            if (variableState.variable.errorMessage(variableState.state.value) != null) {
                                errorsCount++
                            }
                        }
                    }
                    if (errorsCount > 0) errorsCount.toString() else ""
                }
            }

        private suspend fun sectionBudgetTextState(variableStates: Collection<VariableState>): State<String> =
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

        private fun createVariableState(
            variable: Variable,
            lastUpdate: MutableSharedFlow<String>,
            index: Int,
            value: String = variable.default,
        ): VariableState {
            val state: MutableState<String> = mutableStateOf(value)
            return VariableState(variable, state, index) {
                setState(variable, index, state, it, lastUpdate)
            }
        }

        private fun setState(
            variable: Variable,
            index: Int,
            state: MutableState<String>,
            newContent: String,
            lastUpdate: MutableSharedFlow<String>,
        ) {
            val currentContent = state.value
            if (newContent != currentContent) {
                state.value = newContent
                lastUpdate.tryEmit(variable.namespace)
                InputHandler.execute(
                    ValueOperation.Update(
                        variable.namespace,
                        index,
                        variable.name,
                        newContent
                    )
                )
            }
        }
    }
}

@Stable
class VariableState(
    val variable: Variable,
    val state: MutableState<String>,
    val index: Int = Variable.SECTION_VARIABLE_INDEX,
    val setter: (String) -> Unit,
) {
    val hash = index.hashCode().addHash(variable.name).addHash(variable.namespace)

    override fun hashCode() = hash

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as VariableState
        if (variable.namespace != other.variable.namespace) return false
        if (variable.name != other.variable.name) return false
        if (index != other.index) return false
        return true
    }

    override fun toString(): String {
        return state.value
    }
}

@Stable
class RecordState(
    val record: Record,
    val tuples: SnapshotStateList<ImmutableMap<String, VariableState>>,
    val maxIndex: MutableState<Int>,
    val badgeText: State<String>,
) {
    override fun toString(): String {
        return "RecordTuplesState(record=$record)"
    }
}

@Stable
class SectionState(
    val section: Section,
    val variables: ImmutableList<VariableState>,
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
    evaluate(writer, templateState.currentEnvironment())
    writer.toString()
} catch (e: Exception) {
    Teller.error("error while evaluating template: ${templateState.templateName}", e)
    errorHtmlPage(e.message ?: "something went wrong while evaluating this HTML content!")
}