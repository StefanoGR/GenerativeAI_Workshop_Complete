package com.ntt.generativeai.maps

import com.google.android.gms.maps.model.LatLng

sealed class MapsEvent{
    data class OnPermissionSuccessEvent(
        val currentLatLng: LatLng? = null
    ) : MapsEvent()
}