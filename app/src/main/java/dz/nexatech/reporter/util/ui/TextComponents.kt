@file:OptIn(ExperimentalFoundationApi::class)

package dz.nexatech.reporter.util.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import dz.nexatech.reporter.client.R

const val ATTACHED_URL_TAG = "url"

@Composable
fun rememberTextWithLink(
    @StringRes prefix: Int,
    @StringRes label: Int,
    @StringRes suffix: Int,
    separator1: Char = ' ',
    separator2: Char = ' ',
): AnnotatedString {
    val prefixText = stringRes(prefix)
    val linkText = stringRes(label)
    val suffixText = stringRes(suffix)
    val primaryColor = Theme.colorScheme.primary
    val text = remember(prefixText, linkText, suffixText) {
        AnnotatedString.Builder(128).apply {
            append(prefixText)
            append(separator1)
            pushStringAnnotation(tag = ATTACHED_URL_TAG, annotation = ATTACHED_URL_TAG)
            withStyle(SpanStyle(color = primaryColor, fontWeight = FontWeight.Bold)) {
                append(linkText)
            }
            pop()
            append(separator2)
            append(suffixText)
        }.toAnnotatedString()
    }
    return text
}

@Composable
fun SurroundedLink(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    style: TextStyle = Theme.typography.bodyMedium,
    softWrap: Boolean = true,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    onClick: () -> Unit,
) {
    ClickableText(
        text = text,
        style = style,
        modifier = modifier.textPadding(),
        softWrap = softWrap,
        overflow = overflow,
        maxLines = maxLines,
        onTextLayout = onTextLayout,
        onClick = { offset ->
            if (text.hasStringAnnotations(ATTACHED_URL_TAG, offset, offset)) {
                onClick()
            }
        },
    )
}

@Composable
fun Body(
    textRes: Int,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = TextAlign.Center,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current,
) {
    Body(
        text = stringRes(textRes),
        modifier = modifier,
        color = color,
        fontSize = fontSize,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        letterSpacing = letterSpacing,
        textDecoration = textDecoration,
        textAlign = textAlign,
        lineHeight = lineHeight,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        minLines = minLines,
        onTextLayout = onTextLayout,
        style = style,
    )
}

@Composable
fun Body(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = TextAlign.Center,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current,
) {
    Text(
        text,
        modifier.textPadding(),
        color,
        fontSize,
        fontStyle,
        fontWeight,
        fontFamily,
        letterSpacing,
        textDecoration,
        textAlign,
        lineHeight,
        overflow,
        softWrap,
        maxLines,
        minLines,
        onTextLayout,
        style,
    )
}

@Composable
fun Title(
    @StringRes textRes: Int,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    textAlign: TextAlign? = TextAlign.Center,
    lineHeight: TextUnit = TextUnit.Unspecified,
    maxLines: Int = Int.MAX_VALUE,
    style: TextStyle = Theme.typography.titleMedium,
) {
    Body(
        text = stringRes(textRes),
        modifier = modifier,
        color = color,
        fontSize = fontSize,
        textAlign = textAlign,
        lineHeight = lineHeight,
        maxLines = maxLines,
        style = style
    )
}

@Composable
fun Title(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = TextAlign.Center,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = Theme.typography.titleMedium,
) {
    Text(
        text,
        modifier.textPadding(),
        color,
        fontSize,
        fontStyle,
        fontWeight,
        fontFamily,
        letterSpacing,
        textDecoration,
        textAlign,
        lineHeight,
        overflow,
        softWrap,
        maxLines,
        minLines,
        onTextLayout,
        style,
    )
}

@Composable
fun ThemedLink(
    textRes: Int,
    @DrawableRes icon: Int = R.drawable.baseline_link_24,
    onClick: () -> Unit,
) {
    ThemedLink(
        text = stringRes(textRes),
        icon = icon,
        onClick = onClick,
    )
}

@Composable
fun ThemedLink(
    text: String,
    @DrawableRes icon: Int = R.drawable.baseline_link_24,
    onClick: () -> Unit,
) {
    TextButton(
        modifier = Modifier.height(Theme.dimens.link_button_height),
        contentPadding = Theme.dimens.rounded_component,
        onClick = onClick
    ) {
        DecorativeIcon(icon = icon, modifier = Modifier.textPadding())
        Body(
            text = text,
            style = Theme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
        )
    }
}