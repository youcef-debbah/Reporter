package dz.nexatech.reporter.util.ui

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import dagger.hilt.android.internal.ThreadUtil

class DestinationsRegistry {

    private val destinations: MutableMap<String, AbstractDestination> = HashMap()
    private val builders: MutableMap<String, NavGraphBuilder.(NavHostController) -> AbstractDestination> = HashMap()

    fun isEmpty(): Boolean {
        ThreadUtil.ensureMainThread()
        return destinations.isEmpty()
    }

    fun currentDestination(navController: NavHostController): AbstractDestination? =
        destination(navController.currentDestination?.route)

    fun destination(currentRoute: String?): AbstractDestination? {
        ThreadUtil.ensureMainThread()
        return if (currentRoute == null) null else destinations[currentRoute]
    }

    fun register(
        navGraphBuilder: NavGraphBuilder,
        navController: NavHostController,
        builder: NavGraphBuilder.(NavHostController) -> AbstractDestination,
    ): DestinationsRegistry {
        ThreadUtil.ensureMainThread()
        builders[buildDest(navGraphBuilder, navController, builder).route] = builder
        return this
    }

    fun rebuildNavigationGraph(navGraphBuilder: NavGraphBuilder, navController: NavHostController) {
        ThreadUtil.ensureMainThread()
        destinations.clear()
        for (builder in builders.values) {
            buildDest(navGraphBuilder, navController, builder)
        }
    }

    private fun buildDest(
        navGraphBuilder: NavGraphBuilder,
        navController: NavHostController,
        builder: NavGraphBuilder.(NavHostController) -> AbstractDestination,
    ): AbstractDestination {
        val dest = builder.invoke(navGraphBuilder, navController)
        destinations[dest.route] = dest
        return dest
    }

    fun remove(route: String?) {
        ThreadUtil.ensureMainThread()
        destinations.remove(route)
        builders.remove(route)
    }
}