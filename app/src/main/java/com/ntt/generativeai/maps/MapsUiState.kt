package com.ntt.generativeai.maps

import com.google.android.gms.maps.model.LatLng

data class MapsUiState(
    val currentGeolocation: LatLng? = null,
)