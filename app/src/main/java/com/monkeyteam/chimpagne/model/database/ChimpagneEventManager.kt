package com.monkeyteam.chimpagne.model.database

import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import com.monkeyteam.chimpagne.model.location.Location
import kotlinx.coroutines.tasks.await

class ChimpagneEventManager(private val events: CollectionReference) {
    fun getAllEvents(onSuccess: (List<ChimpagneEvent>) -> Unit, onFailure: (Exception) -> Unit) {
        events.get().addOnSuccessListener {
            onSuccess(it.toObjects<ChimpagneEvent>())
        }.addOnFailureListener {
            onFailure(it)
        }
    }

    fun getAllEventsByFilter(
        filter: Filter, onSuccess: (List<ChimpagneEvent>) -> Unit, onFailure: (Exception) -> Unit
    ) {
        events.where(filter).get().addOnSuccessListener {
            onSuccess(it.toObjects<ChimpagneEvent>())
        }.addOnFailureListener {
            onFailure(it)
        }
    }

    fun getAllEventsByFilterAroundLocation(
        center: Location,
        radiusInM: Double,
        filter: Filter,
        onSuccess: (List<ChimpagneEvent>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        // https://firebase.google.com/docs/firestore/solutions/geoqueries?hl=en#kotlin+ktx

        // Each item in 'bounds' represents a startAt/endAt pair. We have to issue
        // a separate query for each pair. There can be up to 9 pairs of bounds
        // depending on overlap, but in most cases there are 4.
        val c = GeoLocation(center.latitude, center.longitude)
        val bounds = GeoFireUtils.getGeoHashQueryBounds(c, radiusInM)
        val tasks: MutableList<Task<QuerySnapshot>> = ArrayList()
        for (b in bounds) {
            val q = events.where(filter).orderBy("location.geohash").startAt(b.startHash).endAt(b.endHash)
            tasks.add(q.get())
        }

        // Collect all the query results together into a single list
        Tasks.whenAllComplete(tasks)
            .addOnCompleteListener {
                val matchingEvents: MutableList<ChimpagneEvent> = ArrayList()
                for (task in tasks) {
                    val snap = task.result
                    for (doc in snap!!.documents) {
                        val event = doc.toObject<ChimpagneEvent>()!!

                        // We have to filter out a few false positives due to GeoHash
                        // accuracy, but most will match
                        val docLocation = GeoLocation(event.location.latitude, event.location.longitude)
                        val distanceInM = GeoFireUtils.getDistanceBetween(docLocation, c)
                        if (distanceInM <= radiusInM) {
                            matchingEvents.add(event)
                        }
                    }
                }

                // matchingEvents contains the results
                onSuccess(matchingEvents)
            }
            .addOnFailureListener {
                onFailure(it)
            }
    }

    fun getEventById(
        id: String, onSuccess: (ChimpagneEvent?) -> Unit, onFailure: (Exception) -> Unit
    ) {
        events.document(id).get().addOnSuccessListener {
            println(it.get("date"))
            onSuccess(it.toObject<ChimpagneEvent>())
        }.addOnFailureListener {
            onFailure(it)
        }
    }

    fun registerEvent(event: ChimpagneEvent, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        updateEvent(event.copy(id = events.document().id), onSuccess, onFailure)
    }
    fun updateEvent(event: ChimpagneEvent, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        events.document(event.id).set(event).addOnSuccessListener {
            onSuccess()
        }.addOnFailureListener {
            onFailure(it)
        }
    }

    fun deleteEvent(id: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        events.document(id).delete().addOnSuccessListener {
            onSuccess()
        }.addOnFailureListener {
            onFailure(it)
        }
    }

    fun addGuestToEvent(event: ChimpagneEvent, user: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        events.document(event.id).update("guests.${user}", true).addOnSuccessListener {
            onSuccess()
        }.addOnFailureListener {
            onFailure(it)
        }
    }

    fun removeGuestFromEvent(event: ChimpagneEvent, user: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        events.document(event.id).update("guests.${user}", FieldValue.delete()).addOnSuccessListener {
            onSuccess()
        }.addOnFailureListener {
            onFailure(it)
        }
    }
}