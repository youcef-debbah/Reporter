@file:OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterialNavigationApi::class,
)

package com.reporter.client.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebView
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.ExperimentalMaterial3Api
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.reporter.client.model.MainViewModel
import com.reporter.client.ui.TemplatesListScreen.addTemplatesListScreen
import com.reporter.util.ui.AbstractActivity
import com.reporter.util.ui.NavigationBarScaffold
import com.reporter.util.ui.addStandardAppBarScreens
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AbstractActivity() {

    val viewModel: MainViewModel by viewModels()

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)

        val webView = createWebView()

        setContent {
            NavigationBarScaffold(TemplatesListScreen) { navController ->
                addStandardAppBarScreens(navController)
                addTemplatesListScreen(navController, webView, viewModel)
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun createWebView(): WebView =
        WebView(this).also {
            it.settings.apply {
                javaScriptEnabled = true
            }
        }
}