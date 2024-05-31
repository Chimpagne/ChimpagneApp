package com.monkeyteam.chimpagne.model.database

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.storage.StorageReference
import com.monkeyteam.chimpagne.model.utils.NoNetworkAvailableException

/**
 * This implementation ensures that the account is always consistent across all clients, even when
 * the account is being updated concurrently from multiple sources.
 */
class AtomicChimpagneAccountManager(
    private val database: Database,
    private val accounts: CollectionReference,
    private val profilePictures: StorageReference
) {
  fun leaveEvent(
      uid: ChimpagneAccountUID,
      eventId: ChimpagneEventId,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    if (!database.connected) {
      onFailure(NoNetworkAvailableException())
      return
    }

    accounts.document(uid).update("joinedEvent.$eventId", FieldValue.delete()).addOnSuccessListener { onSuccess() }.addOnFailureListener(onFailure)
  }
}
