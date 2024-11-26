package com.ntt.generativeai.maps

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
internal fun MapsRoute(
    mapsViewModel: MapsViewModel = viewModel(
        factory = MapsViewModel.getFactory(LocalContext.current.applicationContext)
    )
) {
    val uiState by mapsViewModel.uiState.collectAsStateWithLifecycle()
    MapsScreen(uiState) {
        mapsViewModel.handleEvent(it)
    }

}
