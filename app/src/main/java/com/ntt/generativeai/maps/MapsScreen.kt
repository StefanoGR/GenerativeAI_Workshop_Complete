package com.ntt.generativeai.maps


import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.ntt.generativeai.R
import com.ntt.generativeai.maps.utils.LocationUtils.getCurrentLocation
import com.ntt.generativeai.maps.utils.MapsConstants.DEFAULT_ITALY_LAT
import com.ntt.generativeai.maps.utils.MapsConstants.DEFAULT_ITALY_LONG
import com.ntt.generativeai.maps.utils.MapsConstants.ZOOM_LEVEL_DEFAULT
import com.ntt.generativeai.maps.utils.MapsConstants.ZOOM_LEVEL_WHOLE_MAP

@Composable
fun MapsScreen(
    state: MapsUiState,
    handleEvent: (MapsEvent) -> Unit
) {
    val context = LocalContext.current
    var isMapLoaded by remember { mutableStateOf(false) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(DEFAULT_ITALY_LAT, DEFAULT_ITALY_LONG), ZOOM_LEVEL_WHOLE_MAP
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val locationPermissionsGranted = permissions.values.reduce { acc, isPermissionGranted ->
            acc && isPermissionGranted
        }
        if (!locationPermissionsGranted) {
            Toast.makeText(
                context,
                context.getString(R.string.location_permission_required),
                Toast.LENGTH_SHORT
            ).show()
        } else
            getCurrentLocation(context = context) {
                val currentPosition = it?.firstOrNull()
                    .takeIf { it?.locality != null && it.postalCode != null && it.hasLatitude() && it.hasLongitude() }

                handleEvent.invoke(MapsEvent.OnPermissionSuccessEvent(currentLatLng = currentPosition?.let {
                    LatLng(
                        currentPosition.latitude, currentPosition.longitude
                    )
                }))
            }
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(
            arrayOf(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
        )
    }

    LaunchedEffect(state.currentGeolocation) {
        state.currentGeolocation?.let {
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(
                    it,
                    ZOOM_LEVEL_DEFAULT
                ),
                durationMs = 1
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMapView(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            onMapLoaded = {
                isMapLoaded = true
            },
            state = state
        )
        if (!isMapLoaded) {
            AnimatedVisibility(
                modifier = Modifier.matchParentSize(),
                visible = !isMapLoaded,
                enter = EnterTransition.None,
                exit = fadeOut()
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.background)
                        .wrapContentSize()
                )
            }
        }
    }

}

@Composable
private fun GoogleMapView(
    modifier: Modifier = Modifier,
    cameraPositionState: CameraPositionState = rememberCameraPositionState(),
    onMapLoaded: () -> Unit = {},
    state: MapsUiState
) {
    val context = LocalContext.current
    val styleOption = if (isSystemInDarkTheme()) MapStyleOptions.loadRawResourceStyle(
        context, R.raw.map_night_style
    ) else null
    val mapProperties by remember {
        mutableStateOf(
            MapProperties(
                mapStyleOptions = styleOption
            )
        )
    }
    val uiSettings by remember {
        mutableStateOf(
            MapUiSettings(
                zoomControlsEnabled = false,
                indoorLevelPickerEnabled = false,
                mapToolbarEnabled = false
            )
        )
    }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        properties = mapProperties,
        uiSettings = uiSettings,
        onMapLoaded = onMapLoaded
    ) {
        state.currentGeolocation?.let {
            key(it) {
                MarkerComposable(state = remember {
                    MarkerState(position = it)
                }) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_marker_radio_location),
                        contentDescription = null,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
        }
    }
}