package com.monkeyteam.chimpagne.model.database

import com.google.firebase.firestore.CollectionReference
import kotlinx.coroutines.tasks.await

class ChimpagneEventManager(private val events: CollectionReference) {

    suspend fun getAllEvents(): List<ChimpagneEvent> {
        return events.get().await().toObjects(ChimpagneEvent::class.java)
    }

    suspend fun getEventById(id: String): ChimpagneEvent? {
        return events.document(id).get().await().toObject(ChimpagneEvent::class.java)
    }

    suspend fun updateEvent(event: ChimpagneEvent) {
        events.document(event.id).set(event).await()
    }

    suspend fun deleteEvent(id: String) {
        events.document(id).delete().await()
    }

}