package dz.nexatech.reporter.util.ui

import androidx.compose.material3.Divider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.tooling.preview.Preview
import dz.nexatech.reporter.client.R
import dz.nexatech.reporter.client.model.Variable
import dz.nexatech.reporter.client.model.VariableState
import dz.nexatech.reporter.util.model.Localizer

@Composable
fun VariableInput(variableState: VariableState) {
    PaddedColumn {
        Divider()
        TextInput(variableState)
    }
}

@Composable
private fun TextInput(variableState: VariableState) {
    val value = variableState.state.value
    val variable = variableState.variable
    val length = value.length
    val errorMessage: String? = remember(length) {
        val context = AbstractApplication.INSTANCE
        if (length > variable.max) {
            context.getString(R.string.input_too_long, variable.max)
        } else if (length < variable.min) {
            context.getString(R.string.input_too_short, variable.min)
        } else {
            null
        }
    }
    OutlinedTextField(
        value = value,
        onValueChange = variableState.setter,
        label = { ThemedText(variable.label) },
        prefix = { ThemedText(variable.prefix) },
        suffix = { ThemedText(variable.suffix) },
        isError = errorMessage != null,
        supportingText = { errorMessage?.let { ThemedText(it) } },
    )
}

@Preview(name = "text_input", widthDp = 400, showBackground = true)
@Composable
private fun TextInputPreview() {
    val variable = Variable(
        namespace = "namespace",
        name = "varname",
        required = true,
        type = Variable.Type.TEXT,
        icon = "",
        min = 0,
        max = 1,
        prefix_ar = "",
        prefix_fr = "",
        prefix_en = "",
        suffix_ar = "دج",
        suffix_fr = "DA",
        suffix_en = "DZD",
        default = "joseph",
        label_ar = "label_ar",
        label_fr = "label_fr",
        label_en = "label_en",
        desc_ar = "desc_ar",
        desc_fr = "desc_fr",
        desc_en = "desc_en",
        localizer = Localizer
    )
    val state = rememberSaveable { mutableStateOf(variable.default) }
    val variableState = remember { VariableState(variable, state) { state.value = it } }
    PaddedColumn {
        TextInput(variableState)
    }
}