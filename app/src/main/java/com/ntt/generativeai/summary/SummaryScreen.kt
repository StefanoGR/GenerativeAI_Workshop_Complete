package com.ntt.generativeai.summary

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ntt.generativeai.createPdfFromText


@Composable
fun SummaryScreen(uiState: SummaryUiState) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(15.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(Modifier.height(20.dp))

        Text(
            text = uiState.summary,
        )
        if (uiState.summary.isEmpty())
            CircularProgressIndicator(
                modifier = Modifier.width(64.dp).align(Alignment.CenterHorizontally),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        if (uiState.completed) {
            Row(modifier = Modifier.fillMaxWidth(), Arrangement.Center) {
                Button(
                    onClick = {
                        uiState.summary.createPdfFromText(context)
                    },
                    modifier = Modifier
                        .padding(16.dp),
                    shape = ButtonDefaults.outlinedShape
                ) {
                    Text("Save PDF")
                }
            }
        }
    }
}

@Preview
@Composable
private fun Prev() {
    SummaryScreen(SummaryUiState(""))
}
