package com.ntt.generativeai.maps.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import java.util.concurrent.TimeUnit

object LocationUtils {

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(
        context: Context,
        resultCallback: (List<Address>?) -> Unit
    ) {
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            TimeUnit.SECONDS.toMillis(10)
        ).build()
        val geocoder = Geocoder(context)
        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                getLocationFromGeocoder(location, geocoder, resultCallback)
            } else {
                val locationCallback = object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        locationResult.locations.firstOrNull()?.let { location ->
                            getLocationFromGeocoder(
                                location, geocoder, resultCallback
                            )
                        } ?: resultCallback.invoke(null)
                    }
                }
                fusedLocationProviderClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    context.mainLooper
                )
            }
        }.addOnFailureListener {
            resultCallback.invoke(null)
        }.addOnCanceledListener {
            resultCallback.invoke(null)
        }
    }

    private fun getLocationFromGeocoder(
        location: Location,
        geocoder: Geocoder,
        resultCallback: (List<Address>?) -> Unit,
    ) {
        kotlin.runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder
                    .getFromLocation(
                        location.latitude,
                        location.longitude,
                        1,
                        object : Geocoder.GeocodeListener {
                            override fun onGeocode(addresses: MutableList<Address>) {
                                resultCallback.invoke(addresses)
                            }

                            override fun onError(errorMessage: String?) {
                                super.onError(errorMessage)
                                resultCallback.invoke(null)
                            }

                        })
            } else {
                val addresses =
                    geocoder.getFromLocation(location.latitude, location.longitude, 1)
                resultCallback.invoke(addresses)
            }
        }.onFailure {
            resultCallback.invoke(null)
        }
    }
}