package com.monkeyteam.chimpagne.model.database

import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.monkeyteam.chimpagne.model.location.Location

class ChimpagneAccountManager(private val accounts: CollectionReference) {

  fun GetSpecificAccount(
      userEmail: String,
      onSuccess: (ChimpagneAccount?) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    accounts
        .document(userEmail)
        .get()
        .addOnSuccessListener {
          val data = it.data
          if (data != null) {
            Log.d("ChimpagneAccountManager", "Received data: ${data}")
            val email = data["email"] as String
            val profilePictureUri = data["profilePictureUri"]?.let { Uri.parse(it as String) }
            val firstName = data["firstName"] as String
            val lastName = data["lastName"] as String
            val preferredLanguageEnglish = data["preferredLanguageEnglish"] as Boolean
            val locationMap = data["location"] as Map<*, *>
            val location =
                Location(
                    name = locationMap["name"] as String,
                    latitude = (locationMap["latitude"] as Number).toDouble(),
                    longitude = (locationMap["longitude"] as Number).toDouble(),
                    geohash = locationMap["geohash"] as String)

            val account =
                ChimpagneAccount(
                    email = email,
                    profilePictureUri = profilePictureUri,
                    firstName = firstName,
                    lastName = lastName,
                    preferredLanguageEnglish = preferredLanguageEnglish,
                    location = location)
            onSuccess(account)
          } else {
            Log.d("ChimpagneAccountManager", "Received null data")
            onSuccess(null)
          }
        }
        .addOnFailureListener { onFailure(it) }
  }

  fun UpdateSpecificAccount(
      account: ChimpagneAccount,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    Log.e("ChimpagneAccountManager", "Updating account: $account")
    val data =
        hashMapOf(
            "email" to account.email,
            "profilePictureUri" to account.profilePictureUri.toString(),
            "firstName" to account.firstName,
            "lastName" to account.lastName,
            "preferredLanguageEnglish" to account.preferredLanguageEnglish,
            "location" to
                hashMapOf(
                    "name" to account.location?.name,
                    "latitude" to account.location?.latitude,
                    "longitude" to account.location?.longitude,
                    "geohash" to account.location?.geohash))
    accounts
        .document(account.email)
        .update(data as Map<String, Any>)
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { onFailure(it) }
  }
}
