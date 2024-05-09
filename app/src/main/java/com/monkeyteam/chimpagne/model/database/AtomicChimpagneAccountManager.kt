package com.monkeyteam.chimpagne.model.database

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.storage.StorageReference

class AtomicChimpagneAccountManager(
    private val database: Database,
    private val accounts: CollectionReference,
    private val profilePictures: StorageReference
) {
  fun leaveEvent(
      uid: ChimpagneAccountUID,
      eventId: ChimpagneEventId,
      onSuccess: () -> Unit,
      onFailure: () -> Unit
  ) {
    accounts.document(uid).update("joinedEvent.$eventId", FieldValue.delete())
  }
}
