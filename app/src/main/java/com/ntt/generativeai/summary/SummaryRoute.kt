package com.ntt.generativeai.summary

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File

@Composable
internal fun SummaryRoute(
    flow: SharedFlow<Pair<String, Boolean>>
) {
    val messages = remember { StringBuilder("") }
    var displayText by remember { mutableStateOf("") }
    var hasFinished = remember { false }
    LaunchedEffect(Unit) {
        flow.collect { newMessage ->
            messages.append(newMessage.first)
            displayText = messages.toString()
            hasFinished = newMessage.second
        }
    }
    SummaryScreen(
        displayText,
        hasFinished
    )
}