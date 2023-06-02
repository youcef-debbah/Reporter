package dz.nexatech.reporter.util.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dz.nexatech.reporter.client.R
import dz.nexatech.reporter.client.model.Variable
import dz.nexatech.reporter.client.model.VariableState
import dz.nexatech.reporter.util.model.Localizer

@Composable
fun VariableInput(
    variableState: VariableState,
    modifier: Modifier = Modifier,
    onError: (String, String?) -> Unit,
) {
    TextInput(variableState, onError, modifier) {
        InputIcon(
            variableState.variable.icon,
            StaticIcon.baseline_keyboard,
        )
    }
}

@Composable
private fun TextInput(
    variableState: VariableState,
    onError: (String, String?) -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable () -> Unit,
) {
    val value = variableState.state.value
    val variable = variableState.variable
    val length = value.length
    val errorMessage: String? = remember(variable.required, variable.max, variable.min, length) {
        val context = AbstractApplication.INSTANCE
        if (variable.required && length == 0) {
            context.getString(R.string.input_required)
        } else if (length > variable.max) {
            context.getString(R.string.input_too_long, variable.max)
        } else if (length < variable.min) {
            context.getString(R.string.input_too_short, variable.min)
        } else {
            null
        }
    }
    LaunchedEffect(errorMessage) {
        onError(variable.key, errorMessage)
    }
    CentredColumn(
        modifier = modifier.padding(Theme.dimens.content_padding.copy(bottom = 0.dp) * 2),
    ) {
        var showInfo by rememberSaveable { mutableStateOf(false) }
        AnimatedVisibility(visible = showInfo) {
            Body(variable.desc)
        }
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(errorTrailingIconColor = Theme.colorScheme.onSurfaceVariant),
            value = value,
            onValueChange = variableState.setter,
            label = { Body(variable.label) },
            leadingIcon = leadingIcon,
            trailingIcon = { InfoButton(variable) { showInfo = showInfo.not() } },
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
    onClick: () -> Unit
) {
    if (variable.desc.isNotBlank()) {
        IconButton(onClick = onClick) {
            InfoIcon(icon = R.drawable.baseline_info_24, desc = R.string.input_description)
        }
    }
}

@Composable
fun InputIcon(
    icon: String,
    defaultIcon: AbstractIcon,
) {
    DecorativeIcon(iconsAssetsResources[icon]?: defaultIcon)
}

@Composable
fun rememberFakeVar(name: String = "varname", type: String = Variable.Type.TEXT): Variable =
    remember {
        Variable(
            namespace = "namespace",
            name = name,
            required = true,
            type = type,
            icon = "",
            min = 0,
            max = 1,
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
    ScrollableColumn {
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
                            onError = { _, _ -> },
                        )
                        VariableInput(
                            modifier = Modifier.widthLimit(250.dp),
                            variableState = variableState,
                            onError = { _, _ -> },
                        )
                    }
                }
            }
        }
    }
}
