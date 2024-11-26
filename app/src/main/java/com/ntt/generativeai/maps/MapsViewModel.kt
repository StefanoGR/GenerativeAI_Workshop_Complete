package com.ntt.generativeai.maps

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.ntt.generativeai.InferenceModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class MapsViewModel(
    private val inferenceModel: InferenceModel
) : ViewModel() {

    companion object {
        fun getFactory(context: Context) = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val inferenceModel = InferenceModel.getInstance(context)
                return MapsViewModel(inferenceModel) as T
            }
        }
    }

    private val _uiState: MutableStateFlow<MapsUiState> = MutableStateFlow(MapsUiState(null))
    val uiState: StateFlow<MapsUiState> =
        _uiState.asStateFlow()

    fun handleEvent(event: MapsEvent) {
        when (event){
            is MapsEvent.OnPermissionSuccessEvent -> {
                _uiState.value = _uiState.value.copy(
                    currentGeolocation = event.currentLatLng
                )
            }
        }
    }
}