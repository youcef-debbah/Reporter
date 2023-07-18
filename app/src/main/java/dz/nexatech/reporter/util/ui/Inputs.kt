@file:OptIn(ExperimentalMaterial3Api::class)

package dz.nexatech.reporter.util.ui

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.godaddy.android.colorpicker.HsvColor
import com.godaddy.android.colorpicker.harmony.ColorHarmonyMode
import com.godaddy.android.colorpicker.harmony.HarmonyColorPicker
import dz.nexatech.reporter.client.R
import dz.nexatech.reporter.client.common.AbstractLocalizer
import dz.nexatech.reporter.client.common.Texts
import dz.nexatech.reporter.client.model.COLOR_PICKER_SIZE
import dz.nexatech.reporter.client.model.OUTLINED_FIELD_DROP_MENU_OFFSET
import dz.nexatech.reporter.client.model.Variable
import dz.nexatech.reporter.client.model.Variable.Type
import dz.nexatech.reporter.client.model.Variable.Type.Color.formatColor
import dz.nexatech.reporter.client.model.Variable.Type.Date.datePickerDialogProperties
import dz.nexatech.reporter.client.model.VariableState
import dz.nexatech.reporter.util.model.AppConfig
import dz.nexatech.reporter.util.model.Localizer
import dz.nexatech.reporter.util.model.toggle
import java.util.Calendar
import kotlin.math.max

@Composable
fun readOnlyOutlinedTextFieldColors(): TextFieldColors {
    val onSurface = Theme.colorScheme.onSurface
    val outline = Theme.colorScheme.outline
    val onSurfaceVariant = Theme.colorScheme.onSurfaceVariant
    return OutlinedTextFieldDefaults.colors(
        disabledTextColor = onSurface,
        disabledContainerColor = Color.Transparent,
        disabledBorderColor = outline,
        disabledLeadingIconColor = onSurfaceVariant,
        disabledTrailingIconColor = onSurfaceVariant,
        disabledLabelColor = onSurfaceVariant,
        disabledPlaceholderColor = onSurfaceVariant,
        disabledSupportingTextColor = onSurfaceVariant,
        disabledPrefixColor = onSurfaceVariant,
        disabledSuffixColor = onSurfaceVariant,
        errorTrailingIconColor = onSurfaceVariant,
    )
}

@Composable
fun VariableInput(
    variableState: VariableState,
    modifier: Modifier = Modifier,
) {
    when (variableState.variable.type) {
        Type.Switch.name -> SwitchInput(variableState, modifier)
        Type.Options.name -> OptionsInput(variableState, modifier)
        Type.Date.name -> DateInput(variableState, modifier)
        Type.Color.name -> ColorInput(variableState, modifier)
        Type.Counter.name -> CounterInput(variableState, modifier, Type.Counter)
        Type.Decimal.name -> TextInput(variableState, modifier, Type.Decimal)
        Type.Number.name -> TextInput(variableState, modifier, Type.Number)
        Type.Email.name -> TextInput(variableState, modifier, Type.Email)
        Type.Mobile.name -> TextInput(variableState, modifier, Type.Mobile)
        Type.LinePhone.name -> TextInput(variableState, modifier, Type.LinePhone)
        Type.Uri.name -> TextInput(variableState, modifier, Type.Uri)
        Type.Lines.name -> LinesInput(variableState, modifier)
        else -> TextInput(variableState, modifier, Type.Text)
    }
}

@Composable
fun OptionsInput(
    variableState: VariableState,
    modifier: Modifier = Modifier,
) {
    val outlinedFieldDropMenuOffset = remember { AppConfig.get(OUTLINED_FIELD_DROP_MENU_OFFSET).dp }
    val variable = variableState.variable

    val desc = variable.desc
    val options = remember(desc) {
        Type.Options.loadOptions(desc)
    }

    val value = variableState.state.value
    val selection = remember(value) {
        Type.Options.loadSelection(value, options)
    }

    val errorMessage = remember(variable, value) {
        Type.Options.checker.check(variable, value)?.asString(value)
    }

    val menuExpanded = rememberSaveable { mutableStateOf(false) }

    val clickable = variable.max > 0 && options.isNotEmpty()
    val iconInfo = if (menuExpanded.value) {
        Pair(R.drawable.baseline_arrow_drop_up_24, R.string.hide_options_menu)
    } else {
        Pair(R.drawable.baseline_arrow_drop_down_24, R.string.show_options_menu)
    }

    CentredColumn(
        Modifier.padding(Theme.dimens.content_padding.copy(bottom = zero_padding) * 2)
    ) {
        Box {
            OutlinedTextField(
                colors = readOnlyOutlinedTextFieldColors(),
                enabled = false,
                readOnly = true,
                modifier = modifier.fillMaxWidth(),
                value = value,
                onValueChange = {},
                label = { Body(variable.label) },
                leadingIcon = { InputIcon(variable, StaticIcon.baseline_list) },
                trailingIcon = {
                    if (clickable) {
                        InfoIcon(icon = iconInfo.first, desc = iconInfo.second)
                    }
                },
                prefix = { Body(variable.prefix) },
                suffix = { Body(variable.suffix) },
                isError = errorMessage != null,
                supportingText = { Body(errorMessage ?: "") },
            )

            Box(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .clickable(
                        onClickLabel = stringRes(iconInfo.second),
                        role = Role.DropdownList,
                    ) {
                        menuExpanded.toggle()
                    }
                    .fillMaxWidth()
                    .height(56.dp)
            ) {}
        }

        DropdownMenu(
            modifier = modifier
                .padding(
                    Theme.dimens.content_padding.copy(
                        top = zero_padding,
                        bottom = zero_padding
                    ) * 2
                )
                .minWidth(150.dp),
            offset = DpOffset(zero_padding, outlinedFieldDropMenuOffset),
            expanded = menuExpanded.value,
            onDismissRequest = { menuExpanded.value = false }) {
            val selectedItemColor = Theme.colorScheme.surfaceVariant

            for (option in options) {
                DropdownMenuTextItem(
                    modifier = if (selection.contains(option)) Modifier.background(selectedItemColor) else Modifier,
                    title = option,
                    icon = null
                ) {
                    val newValue = if (selection.contains(option)) {
                        StringBuilder(value.length - option.length).apply {
                            val iterator = selection.iterator()
                            while (iterator.hasNext()) {
                                val selected = iterator.next()
                                if (selected != option) {
                                    append(selected)
                                    if (iterator.hasNext()) {
                                        append(Type.Options.SEPARATOR)
                                    }
                                }
                            }
                        }.toString()
                    } else if (variable.max == 1L && selection.size < 2) {
                        menuExpanded.value = false
                        option
                    } else if (selection.isEmpty()) {
                        option
                    } else {
                        StringBuilder(value.length + option.length + 1).apply {
                            for (selectedValue in selection) {
                                append(selectedValue)
                                append(Type.Options.SEPARATOR)
                            }
                            append(option)
                        }.toString()
                    }

                    variableState.setter.invoke(newValue)
                }
            }
        }
    }
}

@Composable
fun ColorInput(variableState: VariableState, modifier: Modifier) {
    val colorPickerSize = remember { AppConfig.get(COLOR_PICKER_SIZE).dp }
    val showDialog = rememberSaveable { mutableStateOf(false) }
    val variable = variableState.variable
    val value = variableState.state.value
    val color = remember(value) {
        Type.Color.parseColor(value)?.let { Color(it) }
    }
    val errorMessage = remember(variable, value) {
        Type.Color.checker.check(variable, value)?.asString(value)
    }

    OutlinedTextField(
        singleLine = true,
        modifier = modifier
            .padding(Theme.dimens.content_padding.copy(bottom = zero_padding) * 2)
            .fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            errorTrailingIconColor = Theme.colorScheme.onSurfaceVariant
        ),
        value = value,
        onValueChange = variableState.setter,
        label = { Body(variable.label) },
        leadingIcon = { InputIcon(variable, StaticIcon.baseline_color_lens) },
        trailingIcon = {
            IconButton(onClick = { showDialog.toggle() }) {
                if (color != null) {
                    InfoIcon(
                        modifier = Modifier.border(
                            small_padding,
                            Theme.colorScheme.onSurfaceVariant,
                            CircleShape
                        ),
                        icon = R.drawable.baseline_circle_24, desc = R.string.show_color_picker,
                        tint = color,
                    )
                } else {
                    InfoIcon(
                        icon = R.drawable.baseline_edit_24, desc = R.string.show_color_picker,
                    )
                }
            }
        },
        prefix = { Body(variable.prefix) },
        suffix = { Body(variable.suffix) },
        isError = errorMessage != null,
        supportingText = { Body(errorMessage ?: "") },
    )

    if (showDialog.value) {
        val selectedColor = remember(color) {
            mutableStateOf(HsvColor.from(color ?: Color.White))
        }
        val onClose = { showDialog.value = false }
        val onSave = {
            variableState.setter.invoke(formatColor(selectedColor.value.toColor()))
            onClose()
        }
        val onReset = {
            variableState.setter("")
            onClose()
        }

        AlertDialog(properties = datePickerDialogProperties, onDismissRequest = onClose) {
            ContentCard {
                CentredColumn {
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
                    PaddedDivider()
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        HarmonyColorPicker(
                            modifier = Modifier.size(colorPickerSize),
                            harmonyMode = ColorHarmonyMode.NONE,
                            color = selectedColor.value,
                            onColorChanged = { selectedColor.value = it })
                    }
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

@Composable
fun DateInput(variableState: VariableState, modifier: Modifier) {
    val showDialog = rememberSaveable { mutableStateOf(false) }
    val variable = variableState.variable
    val localizer = variable.localizer
    val value = variableState.state.value
    val epoch = remember(value) { localizer.parseSimpleDate(value) }
    val errorMessage = remember(variable, value) {
        Type.Date.checker.check(variable, value)?.asString(value)
    }

    OutlinedTextField(
        singleLine = true,
        modifier = modifier
            .padding(Theme.dimens.content_padding.copy(bottom = zero_padding) * 2)
            .fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(errorTrailingIconColor = Theme.colorScheme.onSurfaceVariant),
        value = value,
        onValueChange = variableState.setter,
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
            val minDate = AbstractLocalizer.newCalendar().apply { timeInMillis = variable.min }
            val maxDate = AbstractLocalizer.newCalendar().apply { timeInMillis = variable.max }
            IntRange(minDate.get(Calendar.YEAR), maxDate.get(Calendar.YEAR))
        }
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = if (errorMessage != null) variable.min else epoch,
            yearRange = yearsRange,
        )

        val onClose = { showDialog.value = false }
        val onSave = {
            val date = localizer.formatSimpleDate(datePickerState.selectedDateMillis)
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
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
fun SwitchInput(
    variableState: VariableState,
    modifier: Modifier,
) {
    val variable = variableState.variable
    val value = variableState.state.value
    val errorMessage: String? = remember(variable, value) {
        Type.Switch.checker.check(variable, value)?.asString(value)
    }

    CentredColumn(
        modifier
            .padding(Theme.dimens.content_padding.copy(bottom = zero_padding) * 2)
            .fillMaxWidth()
    ) {
        CentredRow(Modifier.fillMaxWidth()) {
            val enabled = value.toBooleanStrictOrNull()
            CentredColumn(Modifier.weight(1f)) {
                Body(
                    text = variable.label,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start,
                    style = Theme.typography.titleSmall,
                )
                Body(
                    text = variable.desc,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start,
                )
            }
            CentredRow {
                FilledIconToggleButton(checked = enabled == true, onCheckedChange = {
                    variableState.setter.invoke(if (it) Texts.TRUE else "")
                }) {
                    InfoIcon(
                        icon = R.drawable.baseline_done_24,
                        desc = R.string.enable
                    )
                }
                FilledIconToggleButton(checked = enabled == false, onCheckedChange = {
                    variableState.setter.invoke(if (it) Texts.FALSE else "")
                }) {
                    InfoIcon(
                        icon = R.drawable.baseline_close_24,
                        desc = R.string.disable
                    )
                }
            }
        }
        AnimatedVisibility(errorMessage != null) {
            Body(
                text = errorMessage ?: "",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start,
                color = Theme.colorScheme.error,
                style = Theme.typography.bodySmall,
            )
        }
    }
}

@Composable
private fun CounterInput(
    variableState: VariableState,
    modifier: Modifier = Modifier,
    inputType: Variable.TextType,
) {
    val variable = variableState.variable
    val value = variableState.state.value
    val errorMessage: String? = remember(variable, value) {
        inputType.checker.check(variable, value)?.asString(value)
    }
    val intValue = value.toIntOrNull()
    CentredColumn(
        modifier = modifier
            .padding(Theme.dimens.content_padding.copy(bottom = zero_padding))
            .fillMaxWidth()
    ) {
        val showInfo = rememberSaveable { mutableStateOf(false) }
        AnimatedVisibility(visible = showInfo.value) {
            Body(variable.desc)
        }
        Row {
            FilledIconButton(
                modifier = Modifier.padding(top = 12.dp),
                enabled = intValue != null,
                onClick = {
                    if (intValue != null) {
                        variableState.setter.invoke(intValue.dec().toString())
                    }
                }) {
                InfoIcon(icon = R.drawable.baseline_remove_24, desc = R.string.dec_counter)
            }
            OutlinedTextField(
                singleLine = true,
                modifier = Modifier
                    .padding(
                        Theme.dimens.content_padding.copy(
                            top = zero_padding,
                            bottom = zero_padding
                        ) * 2
                    )
                    .weight(1f),
                colors = OutlinedTextFieldDefaults.colors(errorTrailingIconColor = Theme.colorScheme.onSurfaceVariant),
                value = value,
                onValueChange = variableState.setter,
                label = { Body(variable.label) },
                leadingIcon = { InputIcon(variable, inputType.defaultIcon) },
                trailingIcon = { InfoButton(variable) { showInfo.toggle() } },
                prefix = { Body(variable.prefix) },
                suffix = { Body(variable.suffix) },
                isError = errorMessage != null,
                supportingText = { Body(errorMessage ?: "") },
                keyboardOptions = inputType.keyboardOptions,
            )
            FilledIconButton(
                modifier = Modifier.padding(top = 12.dp),
                enabled = intValue != null,
                onClick = {
                    if (intValue != null) {
                        variableState.setter.invoke(intValue.inc().toString())
                    }
                }) {
                InfoIcon(icon = R.drawable.baseline_add_24, desc = R.string.inc_counter)
            }
        }
    }
}

@Composable
private fun TextInput(
    variableState: VariableState,
    modifier: Modifier = Modifier,
    inputType: Variable.TextType,
) {
    val variable = variableState.variable
    val value = variableState.state.value
    val errorMessage: String? = remember(variable, value) {
        inputType.checker.check(variable, value)?.asString(value)
    }
    CentredColumn(
        modifier = modifier.padding(Theme.dimens.content_padding.copy(bottom = zero_padding) * 2),
    ) {
        val showInfo = rememberSaveable { mutableStateOf(false) }
        AnimatedVisibility(visible = showInfo.value) {
            Body(variable.desc)
        }
        OutlinedTextField(
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(errorTrailingIconColor = Theme.colorScheme.onSurfaceVariant),
            value = value,
            onValueChange = variableState.setter,
            label = { Body(variable.label) },
            leadingIcon = { InputIcon(variable, inputType.defaultIcon) },
            trailingIcon = { InfoButton(variable) { showInfo.toggle() } },
            prefix = { Body(variable.prefix) },
            suffix = { Body(variable.suffix) },
            isError = errorMessage != null,
            supportingText = { Body(errorMessage ?: "") },
            keyboardOptions = inputType.keyboardOptions,
        )
    }
}

@Composable
private fun LinesInput(
    variableState: VariableState,
    modifier: Modifier = Modifier,
) {
    val variable = variableState.variable
    val value = variableState.state.value
    val errorMessage: String? = remember(variable, value) {
        Type.Lines.checker.check(variable, value)?.asString(value)
    }
    CentredColumn(
        modifier = modifier.padding(Theme.dimens.content_padding.copy(bottom = zero_padding) * 2),
    ) {
        val showInfo = rememberSaveable { mutableStateOf(false) }
        AnimatedVisibility(visible = showInfo.value) {
            Body(variable.desc)
        }
        OutlinedTextField(
            minLines = max(variable.min.toInt(), 2),
            maxLines = variable.max.toInt(),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(errorTrailingIconColor = Theme.colorScheme.onSurfaceVariant),
            value = value,
            onValueChange = variableState.setter,
            label = { Body(variable.label) },
            leadingIcon = { InputIcon(variable, Type.Lines.defaultIcon) },
            trailingIcon = { InfoButton(variable) { showInfo.toggle() } },
            prefix = { Body(variable.prefix) },
            suffix = { Body(variable.suffix) },
            isError = errorMessage != null,
            supportingText = { Body(errorMessage ?: "") },
            keyboardOptions = Type.Lines.keyboardOptions,
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
            InfoIcon(icon = R.drawable.baseline_info_24, desc = R.string.input_desc)
        }
    }
}

@Composable
fun InputIcon(
    variable: Variable,
    defaultIcon: AbstractIcon,
) {
    DecorativeIcon(
        icon = iconsAssetsResources[variable.iconPath] ?: defaultIcon,
        tint = LocalContentColor.current.disabled(),
    )
}

@Composable
fun rememberFakeVar(name: String = "varname", type: String = Type.Text.name): Variable =
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
            localizer = Localizer.from("en")
        )
    }

@Preview(name = "input_preview", widthDp = 400, showBackground = true)
@Composable
private fun InputPreview() {
    ScrollableColumn(Modifier.contentPadding()) {
        ContentCard(Modifier.width(700.dp)) {
            val variable = rememberFakeVar()
            val state = rememberSaveable { mutableStateOf(variable.default) }
            val variableState =
                remember { VariableState.from(variable, state) { state.value = it } }
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
