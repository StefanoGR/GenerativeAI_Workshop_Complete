package com.ntt.generativeai.summary

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.ntt.generativeai.InferenceModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.launch

class SummaryViewModel(
    private val inferenceModel: InferenceModel
) : ViewModel() {

    companion object {
        fun getFactory(context: Context) = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val inferenceModel = InferenceModel.getInstance(context)
                return SummaryViewModel(inferenceModel) as T
            }
        }
    }

    init {
        //TODO remove
        createSummary("Android is a mobile operating system based on a modified version of the Linux kernel and other open-source software, designed primarily for touchscreen-based mobile devices such as smartphones and tablets. It is the world's most widely used operating system due to it being used on most smartphones and tablets outside of iPhones and iPads, which use Apple's iOS and iPadOS,[a] respectively. As of October 2024, Android accounts for 45% of the global operating system market, followed by Windows with 26%.[4]\n" +
                "\n" +
                "Android has historically been developed by a consortium of developers known as the Open Handset Alliance, but its most widely used version is primarily developed by Google. It was unveiled in November 2007, with the first commercial Android device, the HTC Dream, being launched in September 2008.\n" +
                "\n" +
                "At its core, the operating system is known as the Android Open Source Project (AOSP)[5] and is free and open-source software (FOSS) primarily licensed under the Apache License. However, most devices run the proprietary Android version developed by Google, which ships with additional proprietary closed-source software pre-installed,[6] most notably Google Mobile Services (GMS),[7] which includes core apps such as Google Chrome, the digital distribution platform Google Play, and the associated Google Play Services development platform. Firebase Cloud Messaging is used for push notifications. While AOSP is free, the \"Android\" name and logo are trademarks of Google, which imposes standards to restrict the use of Android branding by \"uncertified\" devices outside their ecosystem.[8][9]")
    }

    private val _uiState: MutableStateFlow<SummaryUiState> = MutableStateFlow(SummaryUiState(""))
    val uiState: StateFlow<SummaryUiState> =
        _uiState.asStateFlow()


    fun createSummary(text: String) {
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                val result = StringBuilder("")
                inferenceModel.generateResponseAsync(text)
                inferenceModel.partialResults
                    .collectIndexed { index, (partialResult, done) ->
                        result.append(partialResult)
                        _uiState.value = SummaryUiState(result.toString())
                        if (done) {
                            //TODO
                        }
                    }
            }.onFailure { //error
            }
        }
    }
}

