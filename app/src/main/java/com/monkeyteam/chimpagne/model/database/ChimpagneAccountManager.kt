package com.monkeyteam.chimpagne.model.database

import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.toObject
import com.monkeyteam.chimpagne.model.location.Location

class
ChimpagneAccountManager(private val accounts: CollectionReference) {

  fun getAccountByEmail(
    email: String, onSuccess: (ChimpagneAccount?) -> Unit, onFailure: (Exception) -> Unit
  ) {
    accounts.document(email).get()
      .addOnSuccessListener { onSuccess(it.toObject<ChimpagneAccount>()) }
      .addOnFailureListener { onFailure(it) }
  }

  fun updateAccount(
    account: ChimpagneAccount, onSuccess: () -> Unit, onFailure: (Exception) -> Unit
  ) {
    accounts.document(account.email).set(account).addOnSuccessListener { onSuccess() }
      .addOnFailureListener { onFailure(it) }
  }


  fun deleteAccount(email: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    accounts.document(email).delete().addOnSuccessListener { onSuccess() }
      .addOnFailureListener { onFailure(it) }
  }

  fun accountExists(
    email: String, onSuccess: (Boolean) -> Unit, onFailure: (Exception) -> Unit
  ) {
    getAccountByEmail(email, {
      if (it == null) onSuccess(true) else onSuccess(false)
    }, onFailure)
  }
}
