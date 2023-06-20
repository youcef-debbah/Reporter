@file:OptIn(ExperimentalMaterial3Api::class)

package dz.nexatech.reporter.util.ui

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import dz.nexatech.reporter.client.R
import dz.nexatech.reporter.client.model.Variable
import dz.nexatech.reporter.client.model.Variable.Type.Date.formatTemplateDate
import dz.nexatech.reporter.client.model.Variable.Type.Date.parseTemplateDate
import dz.nexatech.reporter.client.model.VariableState
import dz.nexatech.reporter.util.model.Localizer
import dz.nexatech.reporter.util.model.toggle
import java.util.Calendar

val datePickerDialogProperties = DialogProperties(usePlatformDefaultWidth = false)

@Composable
fun VariableInput(
    variableState: VariableState,
    modifier: Modifier = Modifier,
) {
    when (variableState.variable.type) {
        Variable.Type.Date.name -> {
            DateInput(variableState, modifier)
        }

        else -> {
            TextInput(variableState, modifier)
        }
    }
}

@Composable
fun DateInput(variableState: VariableState, modifier: Modifier) {
    val showDialog = rememberSaveable { mutableStateOf(false) }
    val variable = variableState.variable
    val value = variableState.state.value
    val errorMessage = remember(variable, value) {
        Variable.Type.Date.checker.check(variable, value)?.asString(value)
    }

    OutlinedTextField(
        readOnly = true,
        modifier = modifier
            .padding(Theme.dimens.content_padding.copy(bottom = zero_padding) * 2)
            .fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(errorTrailingIconColor = Theme.colorScheme.onSurfaceVariant),
        value = value,
        onValueChange = {},
        label = { Body(variable.label) },
        leadingIcon = { InputIcon(variable, StaticIcon.baseline_event) },
        trailingIcon = {
            IconButton(onClick = { showDialog.toggle() }) {
                InfoIcon(icon = R.drawable.baseline_edit_24, desc = R.string.show_date_picker)
            }
        },
        prefix = { Body(variable.prefix) },
        suffix = { Body(variable.suffix) },
        isError = errorMessage != null,
        supportingText = { Body(errorMessage ?: "") },
    )

    if (showDialog.value) {
        val yearsRange = remember(variable.min, variable.max) {
            val minDate = Calendar.getInstance().apply { timeInMillis = variable.min }
            val maxDate = Calendar.getInstance().apply { timeInMillis = variable.max }
            IntRange(minDate.get(Calendar.YEAR), maxDate.get(Calendar.YEAR))
        }
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = parseTemplateDate(value),
            yearRange = yearsRange,
        )

        val onClose = { showDialog.value = false }
        val onSave = {
            val date =
                formatTemplateDate(datePickerState.selectedDateMillis)
            variableState.setter.invoke(date ?: "")
            onClose()
        }
        val onReset = {
            variableState.setter("")
            onClose()
        }

        AlertDialog(properties = datePickerDialogProperties, onDismissRequest = onClose) {
            ContentCard {
                if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        CentredColumn {
                            IconButton(modifier = Modifier.buttonPadding(), onClick = onSave) {
                                InfoIcon(icon = R.drawable.baseline_done_24, desc = R.string.save)
                            }

                            IconButton(modifier = Modifier.buttonPadding(), onClick = onReset) {
                                InfoIcon(
                                    icon = R.drawable.baseline_delete_forever_24,
                                    desc = R.string.reset
                                )
                            }

                            IconButton(modifier = Modifier.buttonPadding(), onClick = onClose) {
                                InfoIcon(
                                    icon = R.drawable.baseline_close_24,
                                    desc = R.string.cancel
                                )
                            }
                        }
                        VariableDatePicker(variable, datePickerState, false)
                    }
                } else {
                    CentredColumn(Modifier.fillMaxWidth()) {
                        VariableDatePicker(variable, datePickerState, true)
                        PaddedDivider()
                        CentredRow(
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            TextButton(modifier = Modifier.buttonPadding(), onClick = onSave) {
                                DecorativeIcon(icon = R.drawable.baseline_done_24)
                                Body(textRes = R.string.save)
                            }

                            TextButton(modifier = Modifier.buttonPadding(), onClick = onReset) {
                                DecorativeIcon(icon = R.drawable.baseline_delete_forever_24)
                                Body(textRes = R.string.reset)
                            }

                            TextButton(modifier = Modifier.buttonPadding(), onClick = onClose) {
                                DecorativeIcon(icon = R.drawable.baseline_close_24)
                                Body(textRes = R.string.cancel)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun VariableDatePicker(
    variable: Variable,
    datePickerState: DatePickerState,
    headline: Boolean,
) {
    DatePicker(
        title = null,
        headline = if (headline) ({
            Title(
                modifier = Modifier
                    .contentPadding(
                        top = Theme.dimens.content_padding.top * 2,
                        start = Theme.dimens.content_padding.start * 3,
                        end = Theme.dimens.content_padding.end * 3,
                        bottom = Theme.dimens.content_padding.end * 3,
                    )
                    .fillMaxWidth(),
                text = variable.desc,
            )
        }) else null,
        state = datePickerState,
        modifier = Modifier.contentPadding(),
        dateValidator = { it <= variable.max && it >= variable.min },
        showModeToggle = false,
    )
}

@Composable
private fun TextInput(
    variableState: VariableState,
    modifier: Modifier = Modifier,
) {
    val variable = variableState.variable
    val value = variableState.state.value
    val errorMessage: String? = remember(variable, value) {
        Variable.Type.Text.checker.check(variable, value)?.asString(value)
    }
    CentredColumn(
        modifier = modifier.padding(Theme.dimens.content_padding.copy(bottom = zero_padding) * 2),
    ) {
        val showInfo = rememberSaveable { mutableStateOf(false) }
        AnimatedVisibility(visible = showInfo.value) {
            Body(variable.desc)
        }
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(errorTrailingIconColor = Theme.colorScheme.onSurfaceVariant),
            value = value,
            onValueChange = variableState.setter,
            label = { Body(variable.label) },
            leadingIcon = { InputIcon(variable, StaticIcon.baseline_keyboard) },
            trailingIcon = { InfoButton(variable) { showInfo.toggle() } },
            prefix = { Body(variable.prefix) },
            suffix = { Body(variable.suffix) },
            isError = errorMessage != null,
            supportingText = { Body(errorMessage ?: "") },
        )
    }
}

@Composable
private fun InfoButton(
    variable: Variable,
    onClick: () -> Unit,
) {
    if (variable.desc.isNotBlank()) {
        IconButton(onClick = onClick) {
            InfoIcon(icon = R.drawable.baseline_info_24, desc = R.string.input_description)
        }
    }
}

@Composable
fun InputIcon(
    variable: Variable,
    defaultIcon: AbstractIcon,
) {
    DecorativeIcon(iconsAssetsResources[variable.icon] ?: defaultIcon)
}

@Composable
fun rememberFakeVar(name: String = "varname", type: String = Variable.Type.Text.name): Variable =
    remember {
        Variable(
            namespace = "namespace",
            name = name,
            required = true,
            type = type,
            icon = "",
            min = 0L,
            max = 1L,
            prefix_ar = "",
            prefix_fr = "",
            prefix_en = "",
            suffix_ar = "دج",
            suffix_fr = "DA",
            suffix_en = "DZD",
            default = "12345678",
            label_ar = "label_ar",
            label_fr = "label_fr",
            label_en = "label_en",
            desc_ar = "desc_ar",
            desc_fr = "desc_fr",
            desc_en = "desc_en",
            localizer = Localizer
        )
    }

@Preview(name = "input_preview", widthDp = 400, showBackground = true)
@Composable
private fun InputPreview() {
    ScrollableColumn(Modifier.contentPadding()) {
        ContentCard(Modifier.width(700.dp)) {
            val variable = rememberFakeVar()
            val state = rememberSaveable { mutableStateOf(variable.default) }
            val variableState = remember { VariableState(variable, state) { state.value = it } }
            PaddedColumn {
                repeat(2) {
                    Line {
                        VariableInput(
                            modifier = Modifier.widthLimit(150.dp),
                            variableState = variableState,
                        )
                        VariableInput(
                            modifier = Modifier.widthLimit(250.dp),
                            variableState = variableState,
                        )
                    }
                }
            }
        }
    }
}
