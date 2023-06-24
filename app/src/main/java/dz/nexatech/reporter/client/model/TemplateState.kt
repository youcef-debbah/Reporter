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
import dz.nexatech.reporter.client.common.withIO
import dz.nexatech.reporter.client.common.withMain
import dz.nexatech.reporter.client.core.AbstractValue
import dz.nexatech.reporter.client.core.ValueOperation
import dz.nexatech.reporter.client.ui.InputHandler
import dz.nexatech.reporter.util.model.Teller
import dz.nexatech.reporter.util.model.errorHtmlPage
import io.pebbletemplates.pebble.template.PebbleTemplate
import kotlinx.coroutines.async
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

    fun createTuple(
        recordState: RecordState,
        source: ImmutableMap<String, VariableState> = ImmutableMap.of(),
    ) {
        ThreadUtil.ensureMainThread()
        val index = recordState.maxIndex.value + 1
        val variables = recordState.record.variables
        val tupleValues = ArrayList<Value>(variables.size)
        val newTuple = newTuple(variables, index, tupleValues, source)
        InputHandler.execute(ValueOperation.SaveAll(tupleValues))
        recordState.maxIndex.value = index
        recordState.tuples.add(newTuple)
    }

    private fun newTuple(
        variables: ImmutableList<Variable>,
        index: Int,
        tupleValues: ArrayList<Value>,
        source: ImmutableMap<String, VariableState>,
    ): ImmutableMap<String, VariableState> {
        val now = System.currentTimeMillis()
        val tupleBuilder = ImmutableMap.builder<String, VariableState>()
        for (variable in variables) {
            val variableState = source[variable.name]
            val value = variableState?.state?.value ?: variable.default
            tupleBuilder.put(
                variable.name,
                createVariableState(
                    variable = variable,
                    lastUpdate = lastUpdate,
                    index = index,
                    value = value,
                    version = 0,
                ),
            )

            tupleValues.add(
                Value(
                    namespace = variable.namespace,
                    index = index,
                    name = variable.name,
                    lastUpdate = now,
                    content = value,
                )
            )
        }

        return tupleBuilder.build()
    }

    fun moveTupleUp(recordState: RecordState, target: ImmutableMap<String, VariableState>) {
        val tuples = recordState.tuples.toList()
        val targetIndex = tuples.indexOf(target)
        if (targetIndex > 0) {
            swap(target, tuples[targetIndex - 1], recordState)
        }
    }

    fun moveTupleDown(recordState: RecordState, target: ImmutableMap<String, VariableState>) {
        val tuples = recordState.tuples.toList()
        val targetIndex = tuples.indexOf(target)
        if (targetIndex > -1 && targetIndex < tuples.size - 1) {
            swap(target, tuples[targetIndex + 1], recordState)
        }
    }

    private fun swap(
        tuple1: ImmutableMap<String, VariableState>,
        tuple2: ImmutableMap<String, VariableState>,
        recordState: RecordState,
    ) {
        if (tuple1.isNotEmpty() && tuple2.isNotEmpty()) {
            val tuple1Index = tuple1.values.first().index
            val tuple2Index = tuple2.values.first().index

            val valuesCount = tuple1.size + tuple2.size
            val oldValues = ArrayList<Value>(valuesCount)
            val newValues = ArrayList<Value>(valuesCount)

            val new1: ImmutableMap<String, VariableState> =
                copyValues(tuple1, tuple2Index, oldValues, newValues)
            val new2: ImmutableMap<String, VariableState> =
                copyValues(tuple2, tuple1Index, oldValues, newValues)

            InputHandler.execute(
                ValueOperation.ReplaceAll(
                    newValues,
                    oldValues,
                )
            )

            recordState.tuples.replaceAll {
                when (it) {
                    tuple1 -> new2
                    tuple2 -> new1
                    else -> it
                }
            }
        }
    }

    private fun copyValues(
        oldEntries: ImmutableMap<String, VariableState>,
        newIndex: Int,
        oldValues: MutableList<Value>,
        newValues: MutableList<Value>,
    ): ImmutableMap<String, VariableState> {
        val builder = ImmutableMap.Builder<String, VariableState>()
        val now = System.currentTimeMillis()
        for (oldEntry in oldEntries) {
            val oldVariableState = oldEntry.value
            val variable = oldVariableState.variable
            val currentValue = oldVariableState.state.value
            builder.put(
                oldEntry.key,
                createVariableState(
                    variable = variable,
                    lastUpdate = lastUpdate,
                    index = newIndex,
                    value = currentValue,
                    version = oldVariableState.version.inc(),
                )
            )

            oldValues.add(
                Value(
                    namespace = variable.namespace,
                    index = oldVariableState.index,
                    name = variable.name,
                    lastUpdate = now,
                    content = currentValue
                )
            )

            newValues.add(
                Value(
                    namespace = variable.namespace,
                    index = newIndex,
                    name = variable.name,
                    lastUpdate = now,
                    content = currentValue
                )
            )
        }

        return builder.build()
    }

    fun deleteTuple(recordState: RecordState, target: ImmutableMap<String, VariableState>) {
        if (recordState.tuples.remove(target)) {
            val values = target.values
            val toDelete = ArrayList<Value>(values.size)
            val now = System.currentTimeMillis()
            for (variableState in values) {
                val variable = variableState.variable
                toDelete.add(
                    Value(
                        namespace = variable.namespace,
                        index = variableState.index,
                        name = variable.name,
                        lastUpdate = now,
                        content = variable.default
                    )
                )
            }
            InputHandler.execute(ValueOperation.DeleteAll(toDelete))
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
                val sectionsUpdate = async {
                    updateSectionsStates(inputRepository, templateName, sectionsVariableStates)
                }
                val recordsUpdate = async {
                    updateRecordsStates(inputRepository, templateName, recordsStates, lastUpdate)
                }
                sectionsUpdate.await()
                recordsUpdate.await()
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
                updateRecordsStates(loadedValues, recordsStates, lastUpdate)
            }
        }

        private suspend fun updateRecordsStates(
            loadedValues: List<AbstractValue>,
            recordsStates: ImmutableList<RecordState>,
            lastUpdate: MutableSharedFlow<String>,
        ) {
            val loadedTuples = TreeMap<Int, MutableMap<String, AbstractValue>>()
            for (loadedValue in loadedValues) {
                loadedTuples.compute(loadedValue.index) { _, values ->
                    val currentValues = values ?: mutableMapOf()
                    currentValues.apply {
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

            val unusedValues = loadedTuples.values.flatMap { it.values }
            InputHandler.execute(ValueOperation.DeleteAll(unusedValues))
        }

        private suspend fun updateRecordTuples(
            loadedTuples: SortedMap<Int, MutableMap<String, AbstractValue>>,
            record: Record,
            tuples: SnapshotStateList<ImmutableMap<String, VariableState>>,
            maxIndexState: MutableState<Int>,
            lastUpdate: MutableSharedFlow<String>,
        ) {
            val draft = ArrayList<ImmutableMap<String, VariableState>>(loadedTuples.size)
            var maxIndex = Value.INDEX_OFFSET
            for (loadedEntry in loadedTuples.entries) {
                val index = loadedEntry.key
                if (index > maxIndex) {
                    maxIndex = index
                }
                val loadedTuple = loadedEntry.value
                val builder = ImmutableMap.Builder<String, VariableState>()
                for (variable in record.variables) {
                    loadedTuple.remove(variable.key)?.let {
                        builder.put(
                            variable.name,
                            createVariableState(
                                variable = variable,
                                lastUpdate = lastUpdate,
                                index = index,
                                value = it.content,
                                version = 0,
                            ),
                        )
                    }
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
            val loadedValues: List<AbstractValue> =
                inputRepository.loadSectionVariablesValues(templateName)
            for (loadedValue in loadedValues) {
                val variableState = sectionsStates[loadedValue.name]
                if (variableState != null) {
                    variableState.state.value = loadedValue.content
                } else {
                    inputRepository.execute(
                        ValueOperation.Delete(
                            loadedValue.namespace,
                            loadedValue.index,
                            loadedValue.name,
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
                        variable = variable,
                        lastUpdate = lastUpdate,
                        index = Variable.SECTION_VARIABLE_INDEX,
                        version = 0,
                    )
                    allSectionsVariablesBuilder.put(variable.name, variableState)
                    currentSectionVariablesBuilder.add(variableState)
                    if (variable.isFontVariable()) {
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
                val maxIndex = mutableStateOf(Value.INDEX_OFFSET)
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
            version: Int,
        ): VariableState {
            val state: MutableState<String> = mutableStateOf(value)
            return VariableState(variable, state, index, version) {
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
                    ValueOperation.UpdateValue(
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
    val version: Int = 0,
    val setter: (String) -> Unit,
) {
    val hash = version.hashCode().addHash(index).addHash(variable.name).addHash(variable.namespace)

    override fun hashCode() = hash

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as VariableState
        if (variable.namespace != other.variable.namespace) return false
        if (variable.name != other.variable.name) return false
        if (index != other.index) return false
        if (version != other.version) return false
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