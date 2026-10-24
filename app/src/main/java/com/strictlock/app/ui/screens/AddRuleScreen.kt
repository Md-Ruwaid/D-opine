package com.strictlock.app.ui.screens

import androidx.compose.runtime.Composable
import com.strictlock.app.ui.viewmodel.MainViewModel

@Composable
fun AddRuleScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit = {}
) {
    StrictLockScreen(viewModel = viewModel)
}
