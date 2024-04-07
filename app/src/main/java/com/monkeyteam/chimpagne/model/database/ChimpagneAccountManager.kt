package com.monkeyteam.chimpagne.model.database

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.toObject

class ChimpagneAccountManager(private val accounts: CollectionReference) {

  fun GetSpecificAccount(
      userEmail: String,
      onSuccess: (ChimpagneAccount?) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    accounts
        .document(userEmail)
        .get()
        .addOnSuccessListener { onSuccess(it.toObject<ChimpagneAccount>()) }
        .addOnFailureListener { onFailure(it) }
  }
}
