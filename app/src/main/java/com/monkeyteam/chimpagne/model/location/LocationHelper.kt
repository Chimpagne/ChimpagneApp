package com.monkeyteam.chimpagne.model.location

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Response
import org.json.JSONArray

class LocationHelper {

  private val _markers = MutableLiveData<List<Location>>()
  val markers: LiveData<List<Location>> = _markers

  suspend fun convertNameToLocation(name: String, onResult: (Location) -> Unit) {
    val client = OkHttpClient()
    val urlSearchNominatim = "https://nominatim.openstreetmap.org/search?q=$name&format=json"
    val request = okhttp3.Request.Builder().url(urlSearchNominatim).build()
    client
        .newCall(request)
        .enqueue(
            object : Callback {

              override fun onFailure(call: Call, e: IOException) {
                Log.e(
                    "LocationHelper",
                    "Failed to get location for $name, because of IO Exception",
                    e)
                onResult(Location(name))
              }

              override fun onResponse(call: Call, response: Response) {
                response.use {
                  if (!response.isSuccessful) {
                    Log.e(
                        "LocationHelper",
                        "Failed to get location for $name, because of unsuccessful response")
                    onResult(Location(name))
                  } else {
                    val geoLocation = response.body?.string()
                    val jsonArray = geoLocation?.let { JSONArray(it) }
                    if (jsonArray != null && jsonArray.length() > 0) {
                      val jsonObject = jsonArray.getJSONObject(0)
                      val lat = jsonObject.getDouble("lat")
                      val lon = jsonObject.getDouble("lon")
                      val locationInfo = Location(name, lat, lon)
                      onResult(locationInfo)
                    } else {
                      Log.e(
                          "LocationHelper",
                          "Failed to get location for $name, because of empty response")
                      onResult(Location(name))
                    }
                  }
                }
              }
            })
  }

  suspend fun addMarker(location: Location) = withContext(Dispatchers.Main){
    val currentMarkers = _markers.value.orEmpty().toMutableList()
    currentMarkers.add(location)
    _markers.value = currentMarkers
  }

  @Preview
  @Composable
  fun Map() {
    val latMap by remember { mutableDoubleStateOf(46.5196) }
    val lonMap by remember { mutableDoubleStateOf(6.6323) }

    val cameraPositionState = rememberCameraPositionState {
      position = CameraPosition.fromLatLngZoom(LatLng(latMap, lonMap), 10f)
    }

    GoogleMap(
        cameraPositionState = cameraPositionState,
        modifier = Modifier.fillMaxSize(),
        uiSettings = MapUiSettings(zoomControlsEnabled = false)) {
          for (marker in markers.value.orEmpty()) {
            Marker(
                state = rememberMarkerState(position = LatLng(marker.latitude, marker.longitude)),
                title = marker.name)
          }
        }
  }
}
