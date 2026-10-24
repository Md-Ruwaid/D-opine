package com.strictlock.app.ui.screens

import androidx.compose.runtime.Composable
import com.strictlock.app.ui.viewmodel.MainViewModel

@Composable
fun RuleListScreen(
    viewModel: MainViewModel,
    onNavigateToAddRule: () -> Unit = {}
) {
    StrictLockScreen(viewModel = viewModel)
}
