package com.monkeyteam.chimpagne

import android.app.Application
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

class ChimpagneApplication : Application() {
  override fun onCreate() {
    super.onCreate()

    val settings =
        FirebaseFirestoreSettings.Builder()
            .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
            .setPersistenceEnabled(true)
            .build()
    FirebaseFirestore.getInstance().firestoreSettings = settings
  }
}
