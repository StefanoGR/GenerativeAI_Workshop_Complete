package com.ntt.generativeai.llm
import android.content.Context
import android.llama.cpp.LLamaAndroid
import android.util.Log
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.io.File

class LlamaModel : BaseLlm() {
    private val llamaAndroid: LLamaAndroid = LLamaAndroid.instance()

    override suspend fun init(context: Context?) {
        validateModel()
        llamaAndroid.load(MODEL_PATH)
    }

    override val MODEL_PATH: String
        get() = "/data/local/tmp/llm/stablelm-zephyr-3b.Q5_K_M.gguf"

    override suspend fun generateResponseAsync(text: String) {
        llamaAndroid.send( "tell me something about Napoleon")
            .collect { (text, isDone) ->
                _partialResults.emit(text to isDone)
            }
    }

fun createDefaultPrompt(text: String) = "Please provide a comprehensive summary of the following text, capturing the main ideas, key arguments, and essential details while maintaining logical flow." +
        "Aim to reduce the length to 30% of the original while preserving accuracy. This is the text (until the end of prompt):"+
        text

    companion object {
        private var instance: LlamaModel? = null
        fun getInstance(): LlamaModel = instance ?: LlamaModel().also { instance = it }
    }
}
