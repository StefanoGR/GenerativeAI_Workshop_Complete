package com.ntt.generativeai.summary

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable


@Composable
fun SummaryScreen(
    uiState: SummaryUiState,
) {
        Text(
            text = uiState.summary,
        )
}
