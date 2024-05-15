package com.monkeyteam.chimpagne.model.database

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue

class AtomicChimpagneEventManager(
    private val database: Database,
    private val events: CollectionReference
) {
  fun addGuest(
      eventId: ChimpagneEventId,
      userUID: ChimpagneAccountUID,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    events
        .document(eventId)
        .update("guests.${userUID}", true)
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { onFailure(it) }
  }

  fun removeGuest(
      eventId: ChimpagneEventId,
      userUID: ChimpagneAccountUID,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    events
        .document(eventId)
        .update("guests.${userUID}", FieldValue.delete())
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { onFailure(it) }
  }

  fun addStaff(
      eventId: ChimpagneEventId,
      userUID: ChimpagneAccountUID,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    events
        .document(eventId)
        .update("staffs.${userUID}", true)
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { onFailure(it) }
  }

  fun removeStaff(
      eventId: ChimpagneEventId,
      userUID: ChimpagneAccountUID,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    events
        .document(eventId)
        .update("staffs.${userUID}", FieldValue.delete())
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { onFailure(it) }
  }

  fun updateSupply(
      eventId: ChimpagneEventId,
      supply: ChimpagneSupply,
      onSuccess: () -> Unit = {},
      onFailure: (Exception) -> Unit = {}
  ) {
    events
        .document(eventId)
        .update("supplies.${supply.id}", supply)
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener(onFailure)
  }

  fun removeSupply(
      eventId: ChimpagneEventId,
      supplyId: ChimpagneSupplyId,
      onSuccess: () -> Unit = {},
      onFailure: (Exception) -> Unit = {}
  ) {
    events
        .document(eventId)
        .update("supplies.${supplyId}", FieldValue.delete())
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener(onFailure)
  }

  fun assignSupply(
      eventId: ChimpagneEventId,
      supplyId: ChimpagneSupplyId,
      accountUID: ChimpagneAccountUID,
      onSuccess: () -> Unit = {},
      onFailure: (Exception) -> Unit = {}
  ) {
    events
        .document(eventId)
        .update("supplies.$supplyId.assignedTo.$accountUID", true)
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener(onFailure)
  }

  fun unassignSupply(
      eventId: ChimpagneEventId,
      supplyId: ChimpagneSupplyId,
      accountUID: ChimpagneAccountUID,
      onSuccess: () -> Unit = {},
      onFailure: (Exception) -> Unit = {}
  ) {
    events
        .document(eventId)
        .update("supplies.$supplyId.assignedTo.$accountUID", FieldValue.delete())
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener(onFailure)
  }

  fun createPoll(
      eventId: ChimpagneEventId,
      poll: ChimpagnePoll,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    events
        .document(eventId)
        .update("polls.${poll.id}", poll)
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { onFailure(it) }
  }

  fun deletePoll(
      eventId: ChimpagneEventId,
      pollId: ChimpagnePollId,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    events
        .document(eventId)
        .update("polls.${pollId}", FieldValue.delete())
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { onFailure(it) }
  }

  fun castPollVote(
      eventId: ChimpagneEventId,
      pollId: ChimpagnePollId,
      accountUID: ChimpagneAccountUID,
      optionId: ChimpagnePollOptionId,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    events
        .document(eventId)
        .update("polls.${pollId}.votes.${accountUID}", optionId)
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { onFailure(it) }
  }
}
