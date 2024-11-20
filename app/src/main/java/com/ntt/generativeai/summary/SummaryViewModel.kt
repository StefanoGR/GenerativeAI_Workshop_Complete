package com.ntt.generativeai.summary

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.ntt.generativeai.InferenceModel
import com.ntt.generativeai.getBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class SummaryViewModel(
    private val inferenceModel: InferenceModel,
    val scanned: List<File>
) : ViewModel() {

    private val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    companion object {
        fun getFactory(context: Context,  scanned: List<File>) = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val inferenceModel = InferenceModel.getInstance(context)
                return SummaryViewModel(inferenceModel, scanned = scanned) as T
            }
        }
    }
    init {
        analyzeText()
    }

    private val _uiState: MutableStateFlow<SummaryUiState> = MutableStateFlow(SummaryUiState(""))
    val uiState: StateFlow<SummaryUiState> =
        _uiState.asStateFlow()

    fun analyzeText(){
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                val results = scanned.mapIndexed { index, file ->
                    index to processImage(file)
                }.toMap()

                createSummary(results.toSortedMap().values.joinToString(separator =" "))
            }
        }
    }

    private suspend fun processImage(file: File): String = suspendCancellableCoroutine { continuation ->
        try {
            val image = InputImage.fromBitmap(file.getBitmap(), 0)
            textRecognizer.process(image)
                .addOnSuccessListener { visionText ->
                    if (!continuation.isCompleted) {
                        continuation.resume(visionText.text)
                    }
                }
                .addOnFailureListener { e ->
                    if (!continuation.isCompleted) {
                        continuation.resumeWithException(e)
                    }
                }
        } catch (e: Exception) {
            if (!continuation.isCompleted) {
                continuation.resumeWithException(e)
            }
        }
    }


    @OptIn(FlowPreview::class)
    private suspend fun createSummary(text: String) {
            kotlin.runCatching {
                val result = StringBuilder()
                inferenceModel.generateResponseAsync(text)

                // Buffer updates using a flow
                inferenceModel.partialResults
                    .buffer()
                    .debounce(200)
                    .collectIndexed { index, (partialResult, done) ->
                        result.append(partialResult)
                        withContext(Dispatchers.Main) {
                            _uiState.value = SummaryUiState(result.toString(), done)
                        }
                    }
            }.onFailure { error ->
                // Handle error
            }
    }
}

