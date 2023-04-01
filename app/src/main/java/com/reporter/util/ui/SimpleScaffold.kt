@file:OptIn(ExperimentalMaterial3Api::class)

package com.reporter.util.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun SimpleScaffold(
    modifier: Modifier = Modifier,
    columnModifier: Modifier = Modifier,
    columnArrangement: Arrangement.Vertical = Arrangement.Top,
    columnAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    topBar: @Composable () -> Unit = { SimpleAppBar() },
    bottomBar: @Composable () -> Unit = {},
    snackbarHost: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    containerColor: Color = MaterialTheme.colorScheme.background,
    contentColor: Color = contentColorFor(containerColor),
    content: @Composable ColumnScope.() -> Unit
) {
    ApplicationTheme {
        Scaffold(
            modifier = modifier,
            topBar = topBar,
            bottomBar = bottomBar,
            snackbarHost = snackbarHost,
            floatingActionButton = floatingActionButton,
            floatingActionButtonPosition = floatingActionButtonPosition,
            containerColor = containerColor,
            contentColor = contentColor,
            content = { paddingValues ->
                Column(
                    modifier = columnModifier
                        .padding(paddingValues)
                        .fillMaxSize(),
                    verticalArrangement = columnArrangement,
                    horizontalAlignment = columnAlignment,
                ) {
                    content()
                }
            }
        )
    }
}