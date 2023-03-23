package com.reporter.util.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable

/**
 * a destination can be a screen or a graph of screens
 */
@Immutable
open class AbstractDestination(
    /**
     * the destination's unique route
     */
    val route: String,
    /**
     * used as the icon for any button that opens this destination
     * also -it this destination is a screen- it may be shown at the top right next to the title
     */
    @DrawableRes val icon: Int,
    /**
     * used as an icon description for an icon button that opens this destination
     * also -it this destination is a screen- it may be shown at the top (for example inside an app bar)
     */
    @StringRes val title: Int,
    /**
     * used as the text for any button that opens this destination
     */
    @StringRes val label: Int? = null,
) {

    override fun toString(): String = route

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AbstractDestination) return false

        if (route != other.route) return false

        return true
    }

    override fun hashCode(): Int {
        return route.hashCode()
    }
}