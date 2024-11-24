package com.ntt.generativeai.llm

import android.content.Context
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.io.File

class GemmaInferenceModel : BaseLlm() {
    private lateinit var llmInference: LlmInference

    override suspend fun init(context: Context?) {
        validateModel()
        
        val options = LlmInference.LlmInferenceOptions.builder()
            .setModelPath(MODEL_PATH)
            .setMaxTokens(1024)
            .setResultListener { partialResult, done ->
                _partialResults.tryEmit(partialResult to done)
            }
            .build()

        llmInference = LlmInference.createFromOptions(context, options)
    }

    override val MODEL_PATH: String
        get() = "/data/local/tmp/llm/gemma-2b-it-gpu-int4.bin"

    override suspend fun generateResponseAsync(text: String) {
        llmInference.generateResponseAsync(createDefaultPrompt(text))
    }

    fun createDefaultPrompt(text: String) = "Please provide a comprehensive summary of the following text, capturing the main ideas, key arguments, and essential details while maintaining logical flow." +
                "Aim to reduce the length to 30% of the original while preserving accuracy. This is the text (until the end of prompt):"+
                text

    companion object {
        private var instance: GemmaInferenceModel? = null
        fun getInstance(): GemmaInferenceModel = instance ?: GemmaInferenceModel().also { instance = it }
    }

}
