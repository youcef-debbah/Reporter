@file:OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterialNavigationApi::class,
)

package dz.nexatech.reporter.client.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.lifecycle.ViewModelProvider
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import dz.nexatech.reporter.client.model.MainViewModel
import dz.nexatech.reporter.client.ui.TemplatesListScreen.addTemplatesListScreen
import dz.nexatech.reporter.util.ui.AbstractActivity
import dz.nexatech.reporter.util.ui.NavigationBarScaffold
import dz.nexatech.reporter.util.ui.addStandardAppBarScreens
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AbstractActivity() {

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        setContent {
            val viewModel = ViewModelProvider(this)[MainViewModel::class.java]
            NavigationBarScaffold(TemplatesListScreen) { navController ->
                addStandardAppBarScreens(navController)
                addTemplatesListScreen(navController, viewModel)
            }
        }
    }
}