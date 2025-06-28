package dz.nexatech.reporter.util.ui

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import kotlinx.serialization.Serializable

/**
 * a destination can be a screen or a graph of screens
 */
@Stable
@Serializable
abstract class AbstractDestination(
    /**
     * the destination's unique route
     */
    val route: String,
    /**
     * used as the icon for any button that opens this destination
     * also -if this destination is a screen- it may be shown at the top right next to the title
     */
    val icon: AbstractIcon?,

    /**
     * used as a budget on top of icon
     */
    val badgeText: State<String> = mutableStateOf("")
) {

    /**
     * used as an icon description for an icon button that opens this destination
     * also -it this destination is a screen- it may be shown at the top (for example inside an app bar)
     */
    @Composable
    @ReadOnlyComposable
    abstract fun title(): String

    /**
     * used as the text for any button that opens this destination
     */
    @Composable
    @ReadOnlyComposable
    abstract fun label(): String?

    /**
     * used to construct the navigation bar UI for this screen, if this screen is included it will
     * be shown as "selected" in the UI, an empty array will cause the navigation bar to be hidden
     */
    abstract val destinations: List<AbstractDestination>

    override fun toString(): String = route

    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AbstractDestination) return false
        return route == other.route
    }

    final override fun hashCode(): Int {
        return route.hashCode()
    }
}

@Stable
@Serializable
open class StaticScreenDestination(
    val screenRoute: String,
    val screenIcon: AbstractIcon?,
    @StringRes val titleRes: Int,
    @StringRes val labelRes: Int? = null,
) : AbstractDestination(screenRoute, screenIcon) {

    @Composable
    @ReadOnlyComposable
    override fun title(): String = LocalContext.current.getString(titleRes)

    @Composable
    @ReadOnlyComposable
    override fun label(): String? = labelRes?.let { LocalContext.current.getString(labelRes) }

    override val destinations: List<AbstractDestination>
        get() = emptyList()
}

fun AbstractDestination?.destinationsOrEmpty(): List<AbstractDestination> =
    this?.destinations ?: emptyList()

fun NavController.navigate(destinationScreen: AbstractDestination) {
    navigate(destinationScreen.route) {
        launchSingleTop = true
        restoreState = true
    }
}