@file:OptIn(ExperimentalLayoutApi::class)

package dz.nexatech.reporter.util.ui

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import dz.nexatech.reporter.client.R
import dz.nexatech.reporter.client.model.ResourcesRepository
import dz.nexatech.reporter.client.model.Variable
import dz.nexatech.reporter.client.model.VariableState
import dz.nexatech.reporter.util.model.Localizer

@Composable
fun VariableInput(
    variableState: VariableState,
    resourcesRepository: ResourcesRepository,
    modifier: Modifier = Modifier,
    onError: (String, String?) -> Unit,
) {
    TextInput(variableState, onError, modifier) {
        InputIcon(
            resourcesRepository,
            variableState.variable.icon,
            R.drawable.baseline_keyboard_24
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
        modifier = modifier.padding(Theme.dimens.content_padding.copy(bottom = 0.dp)),
    ) {
        var showInfo by rememberSaveable { mutableStateOf(false) }
        AnimatedVisibility(visible = showInfo) {
            Body(variable.desc)
        }
        OutlinedTextField(
            colors = OutlinedTextFieldDefaults.colors(errorTrailingIconColor = Theme.colorScheme.onSurfaceVariant),
            value = value,
            onValueChange = variableState.setter,
            label = { Body(variable.label) },
            leadingIcon = leadingIcon,
            trailingIcon = { InfoButton(variable) { showInfo = showInfo.not() } },
            prefix = { Body(variable.prefix) },
            suffix = { Body(variable.suffix) },
            isError = errorMessage != null,
            supportingText = { errorMessage?.let { Body(it) } },
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
    resourcesRepository: ResourcesRepository,
    icon: String,
    @DrawableRes defaultIcon: Int,
) {
    val painter = iconsAssetsResources[icon]?.painterResource(resourcesRepository)
        ?: painterResource(defaultIcon)
    Icon(painter = painter, contentDescription = null)
}

//@Preview(name = "input_preview", widthDp = 400, showBackground = true)
@Composable
private fun InputPreview() {
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
        TextInput(variableState, { _, _ -> }) {
            DecorativeIcon(icon = R.drawable.baseline_keyboard_24)
        }
    }
}