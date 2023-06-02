@file:OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterialNavigationApi::class,
)

package dz.nexatech.reporter.client.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import dagger.hilt.android.AndroidEntryPoint
import dz.nexatech.reporter.client.model.MainViewModel
import dz.nexatech.reporter.client.ui.TemplatesListScreen.addTemplatesListScreen
import dz.nexatech.reporter.util.ui.AbstractActivity
import dz.nexatech.reporter.util.ui.AbstractApplication
import dz.nexatech.reporter.util.ui.DestinationsRegistry
import dz.nexatech.reporter.util.ui.NavigationScaffold

@AndroidEntryPoint
class MainActivity : AbstractActivity() {

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        val viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        val destinations = viewModel.activeDestinations
        setContent {
            NavigationScaffold(destinations, TemplatesListScreen) { navController ->
                buildNavigationGraph(this, navController, destinations, viewModel)
            }
        }
    }

    private fun buildNavigationGraph(
        navGraphBuilder: NavGraphBuilder,
        navController: NavHostController,
        destinations: DestinationsRegistry,
        viewModel: MainViewModel
    ) {
        if (destinations.isEmpty()) {
            val standardDestinations = AbstractApplication.INSTANCE.config.standardDestinations
            destinations
                .register(navGraphBuilder, navController) {
                    standardDestinations.addSettingsScreen(this, it)
                }
                .register(navGraphBuilder, navController) {
                    addTemplatesListScreen(it, viewModel)
                }
        } else {
            destinations.rebuildNavigationGraph(navGraphBuilder, navController)
        }
    }
}