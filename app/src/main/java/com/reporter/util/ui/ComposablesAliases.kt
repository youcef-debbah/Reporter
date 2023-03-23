@file:Suppress("unused")

package com.reporter.util.ui

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.paging.compose.items
import kotlinx.coroutines.flow.Flow

/**
 * Contains functions to access the current theme values provided at the call site's position in
 * the hierarchy.
 */
object Theme {
    /**
     * Retrieves the current [ColorScheme] at the call site's position in the hierarchy.
     */
    val colorScheme: ColorScheme
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme

    /**
     * Retrieves the current [Typography] at the call site's position in the hierarchy.
     */
    val typography: Typography
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.typography

    /**
     * Retrieves the current [Shapes] at the call site's position in the hierarchy.
     */
    val shapes: Shapes
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.shapes

    /**
     * Retrieves the current [Dimens] at the call site's position in the hierarchy.
     */
    val dimens: Dimens
        @Composable
        @ReadOnlyComposable
        get() = LocalDimens.current
}

/**
 * <a href="https://material.io/design/material-theming/overview.html" class="external" target="_blank">Material Theming</a>.
 *
 * Material Theming refers to the customization of your Material Design app to better reflect your
 * product’s brand.
 *
 * Material components such as [Button] and [Checkbox] use values provided here when retrieving
 * default values.
 *
 * It defines colors as specified in the [Material Color theme creation spec](https://material.io/design/color/the-color-system.html#color-theme-creation),
 * typography defined in the [Material Type Scale spec](https://material.io/design/typography/the-type-system.html#type-scale),
 * and shapes defined in the [Shape scheme](https://material.io/design/shape/applying-shape-to-ui.html#shape-scheme).
 *
 * All values may be set by providing this component with the [colors][Colors],
 * [typography][Typography], and [shapes][Shapes] attributes. Use this to configure the overall
 * theme of elements within this MaterialTheme.
 *
 * Any values that are not set will inherit the current value from the theme, falling back to the
 * defaults if there is no parent MaterialTheme. This allows using a MaterialTheme at the top
 * of your application, and then separate MaterialTheme(s) for different screens / parts of your
 * UI, overriding only the parts of the theme definition that need to change.
 *
 * This is only an alias to (Material v2) MaterialTheme with a distinguishable
 * name that allows for a better auto-import support by the IDE
 *
 * @param colors A complete definition of the Material Color theme for this hierarchy
 * @param typography A set of text styles to be used as this hierarchy's typography system
 * @param shapes A set of shapes to be used by the components in this hierarchy
 */
@Suppress("RemoveRedundantQualifierName")
@Composable
fun M2Theme(
    colors: androidx.compose.material.Colors = androidx.compose.material.MaterialTheme.colors,
    typography: androidx.compose.material.Typography = androidx.compose.material.MaterialTheme.typography,
    shapes: androidx.compose.material.Shapes = androidx.compose.material.MaterialTheme.shapes,
    content: @Composable () -> Unit
) = androidx.compose.material.MaterialTheme(colors, typography, shapes, content)

///**
// * Default [Divider], which can be optionally positioned at the bottom of a
// * dropdown menu item content.
// *
// * This is only an alias to [androidx.compose.material3.MenuDefaults.Divider] with a distinguishable
// * name that allows for a better auto-import support by the IDE
// *
// * @param modifier modifier for the divider's layout
// * @param color color of the divider, null defaults to MenuTokens.DividerColor
// * @param thickness thickness of the divider, null defaults to MenuTokens.DividerHeight
// */
//@Composable
//fun MenuDivider(
//    modifier: Modifier = Modifier,
//    color: Color? = null,
//    thickness: Dp? = null,
//) {
//
//    if (color != null && thickness != null)
//        androidx.compose.material3.MenuDefaults.Divider(modifier, color, thickness)
//    else if (color != null)
//        androidx.compose.material3.MenuDefaults.Divider(modifier, color = color)
//    else if (thickness != null)
//        androidx.compose.material3.MenuDefaults.Divider(modifier, thickness = thickness)
//    else
//        androidx.compose.material3.MenuDefaults.Divider(modifier)
//}
//
///**
// * Default [Divider], which will be positioned at the bottom of the [TabRow], underneath the
// * indicator.
// *
// * This is only an alias to [androidx.compose.material3.TabRowDefaults.Divider] with a distinguishable
// * name that allows for a better auto-import support by the IDE
// *
// * @param modifier modifier for the divider's layout
// * @param thickness thickness of the divider, null defaults to PrimaryNavigationTabTokens.DividerHeight
// * @param color color of the divider, null defaults to MaterialTheme.colorScheme.fromToken(PrimaryNavigationTabTokens.DividerColor)
// */
//@Composable
//fun TabDivider(
//    modifier: Modifier = Modifier,
//    thickness: Dp? = null,
//    color: Color? = null
//) {
//    if (thickness != null && color != null)
//        androidx.compose.material3.TabRowDefaults.Divider(modifier, thickness, color)
//    else if (thickness != null)
//        androidx.compose.material3.TabRowDefaults.Divider(modifier, thickness = thickness)
//    else if (color != null)
//        androidx.compose.material3.TabRowDefaults.Divider(modifier, color = color)
//    else
//        androidx.compose.material3.TabRowDefaults.Divider(modifier)
//}

/**
 * Default indicator, which will be positioned at the bottom of the [TabRow], on top of the
 * divider.
 *
 * This is only an alias to [androidx.compose.material3.TabRowDefaults.Indicator] with a distinguishable
 * name that allows for a better auto-import support by the IDE
 *
 * @param modifier modifier for the indicator's layout
 * @param height height of the indicator, null defaults PrimaryNavigationTabTokens.ActiveIndicatorHeight
 * @param color color of the indicator, null defaults MaterialTheme.colorScheme.fromToken(PrimaryNavigationTabTokens.ActiveIndicatorColor)
 */
@Composable
fun TabIndicator(
    modifier: Modifier = Modifier,
    height: Dp? = null,
    color: Color? = null
) {
    if (height != null && color != null)
        androidx.compose.material3.TabRowDefaults.Indicator(modifier, height, color)
    else if (height != null)
        androidx.compose.material3.TabRowDefaults.Indicator(modifier, height = height)
    else if (color != null)
        androidx.compose.material3.TabRowDefaults.Indicator(modifier, color = color)
    else
        androidx.compose.material3.TabRowDefaults.Indicator(modifier)
}

/**
 * <a href="https://material.io/components/dividers" class="external" target="_blank">Material Design divider</a>.
 *
 * A divider is a thin line that groups content in lists and layouts.
 *
 * ![Dividers image](https://developer.android.com/images/reference/androidx/compose/material/dividers.png)
 *
 * @param color color of the divider line, null defaults to a transparent variant of onSurface theme color.
 * @param thickness thickness of the divider line, 1 dp is used by default. Using [Dp.Hairline]
 * will produce a single pixel divider regardless of screen density.
 * @param startIndent start offset of this line, no offset by default.
 */

@Composable
fun M2Divider(
    modifier: Modifier = Modifier,
    color: Color? = null,
    thickness: Dp = 1.dp,
    startIndent: Dp = 0.dp
) {
    if (color != null)
        androidx.compose.material.Divider(
            modifier = modifier,
            color = color,
            thickness = thickness,
            startIndent = startIndent
        )
    else
        androidx.compose.material.Divider(
            modifier = modifier,
            thickness = thickness,
            startIndent = startIndent
        )
}

/**
 * Adds the [LazyPagingItems] and their content to the scope. The range from 0 (inclusive) to
 * [LazyPagingItems.itemCount] (exclusive) always represents the full range of presentable items,
 * because every event from [PagingDataDiffer] will trigger a recomposition.
 *
 * @sample androidx.paging.compose.samples.ItemsDemo
 *
 * This is only an alias to [androidx.paging.compose.LazyPagingItemsKt.items] with a distinguishable
 * name that allows for a better auto-import support by the IDE
 *
 * @param items the items received from a [Flow] of [PagingData].
 * @param key a factory of stable and unique keys representing the item. Using the same key
 * for multiple items in the list is not allowed. Type of the key should be saveable
 * via Bundle on Android. If null is passed the position in the list will represent the key.
 * When you specify the key the scroll position will be maintained based on the key, which
 * means if you add/remove items before the current visible item the item with the given key
 * will be kept as the first visible one.
 * @param itemContent the content displayed by a single item. In case the item is `null`, the
 * [itemContent] method should handle the logic of displaying a placeholder instead of the main
 * content displayed by an item which is not `null`.
 */
fun <T : kotlin.Any> androidx.compose.foundation.lazy.LazyListScope.pagingItems(
    items: androidx.paging.compose.LazyPagingItems<T>,
    key: ((T) -> kotlin.Any)?,
    itemContent: @androidx.compose.runtime.Composable() (androidx.compose.foundation.lazy.LazyItemScope.(T?) -> kotlin.Unit)
) = items(items, key, itemContent)