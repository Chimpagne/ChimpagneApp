package com.monkeyteam.chimpagne.model.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices

class LocationViewModel(myContext: Context) {
  private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(myContext)

  fun startLocationUpdates(
      myContext: Context,
      onLocationSuccess: (lat: Double, lng: Double) -> Unit
  ) {
    if (ActivityCompat.checkSelfPermission(myContext, Manifest.permission.ACCESS_FINE_LOCATION) ==
        PackageManager.PERMISSION_GRANTED) {
      fusedLocationClient.lastLocation.addOnSuccessListener { location ->
        location?.let { onLocationSuccess(it.latitude, it.longitude) }
      }
    }
  }
}
