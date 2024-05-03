package com.monkeyteam.chimpagne.model.database

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue

class AtomicChimpagneEventManager(
  private val database: Database,
  private val events: CollectionReference
) {
  fun addSupplyAtomically(eventId: ChimpagneEventId, supply: ChimpagneSupply, onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit = {}) {
    events.document(eventId).update("supplies.${supply.id}", supply).addOnSuccessListener { onSuccess() } .addOnFailureListener(onFailure)
  }

  fun removeSupplyAtomically(eventId: ChimpagneEventId, supplyId: ChimpagneSupplyId, onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit = {}) {
    events.document(eventId).update("supplies.${supplyId}", FieldValue.delete()).addOnSuccessListener { onSuccess() } .addOnFailureListener(onFailure)
  }

  fun assignSupplyAtomically(eventId: ChimpagneEventId, supplyId: ChimpagneSupplyId, accountUID: ChimpagneAccountUID, onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit = {}) {
    events.document(eventId).update("supplies.$supplyId.assignedTo.$accountUID", true).addOnSuccessListener { onSuccess() } .addOnFailureListener(onFailure)
  }

  fun unassignSupplyAtomically(eventId: ChimpagneEventId, supplyId: ChimpagneSupplyId, accountUID: ChimpagneAccountUID, onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit = {}) {

  }
}