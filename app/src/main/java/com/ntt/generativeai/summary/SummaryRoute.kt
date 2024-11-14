package com.ntt.generativeai.summary

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
internal fun SummaryRoute(
    summaryViewModel: SummaryViewModel = viewModel(
        factory = SummaryViewModel.getFactory(LocalContext.current.applicationContext)
    )
) {
    val uiState by summaryViewModel.uiState.collectAsStateWithLifecycle()

    SummaryScreen(
        uiState,
    )
}