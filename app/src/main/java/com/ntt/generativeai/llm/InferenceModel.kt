package com.ntt.generativeai.llm
import android.content.Context
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import java.io.File
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class InferenceModel private constructor(context: Context) {
    private var llmInference: LlmInference

    private val modelExists: Boolean
        get() = File(MODEL_PATH).exists()

    private val _partialResults = MutableSharedFlow<Pair<String, Boolean>>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val partialResults: SharedFlow<Pair<String, Boolean>> = _partialResults.asSharedFlow()

    init {
        if (!modelExists) {
            throw IllegalArgumentException("Model not found at path: $MODEL_PATH")
        }

        val options = LlmInference.LlmInferenceOptions.builder()
            .setModelPath(MODEL_PATH)
            .setMaxTokens(1024)
            .setResultListener { partialResult, done ->
                _partialResults.tryEmit(partialResult to done)
            }
            .build()

        llmInference = LlmInference.createFromOptions(context, options)
    }

    fun generateResponseAsync(text: String) {
        val prompt = "Please provide a comprehensive summary of the following text, capturing the main ideas, key arguments, and essential details while maintaining logical flow." +
                "Aim to reduce the length to 30% of the original while preserving accuracy. This is the text (until the end of prompt):"+
                text

        llmInference.generateResponseAsync(prompt)
    }

    companion object {
        private const val MODEL_PATH = "/data/local/tmp/llm/gemma-2b-it-gpu-int4.bin"
        private var instance: InferenceModel? = null

        fun getInstance(context: Context): InferenceModel = instance ?: InferenceModel(context).also { instance = it }
    }
}
