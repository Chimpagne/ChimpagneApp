package com.monkeyteam.chimpagne.model.database

import androidx.core.net.toUri
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.StorageReference
import com.monkeyteam.chimpagne.model.location.Location

class ChimpagneEventManager(
    private val database: Database,
    private val events: CollectionReference,
    private val eventPictures: StorageReference
) {
  val atomic = AtomicChimpagneEventManager(database, events)

  fun getAllEventsByFilterAroundLocation(
      center: Location,
      radiusInM: Double,
      onSuccess: (List<ChimpagneEvent>) -> Unit,
      onFailure: (Exception) -> Unit,
      filter: Filter? = null,
  ) {
    // https://firebase.google.com/docs/firestore/solutions/geoqueries?hl=en#kotlin+ktx

    // Each item in 'bounds' represents a startAt/endAt pair. We have to issue
    // a separate query for each pair. There can be up to 9 pairs of bounds
    // depending on overlap, but in most cases there are 4.
    val c = GeoLocation(center.latitude, center.longitude)
    val bounds = GeoFireUtils.getGeoHashQueryBounds(c, radiusInM)
    val tasks: MutableList<Task<QuerySnapshot>> = ArrayList()
    for (b in bounds) {

      if (filter != null) {
        val q =
            events.where(filter).orderBy("location.geohash").startAt(b.startHash).endAt(b.endHash)
        tasks.add(q.get())
      } else {
        val q = events.orderBy("location.geohash").startAt(b.startHash).endAt(b.endHash)
        tasks.add(q.get())
      }
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
        .addOnFailureListener { onFailure(it) }
  }

  fun getEventById(
      id: String,
      onSuccess: (ChimpagneEvent?) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    events
        .document(id)
        .get()
        .addOnSuccessListener { account ->
          val event = account.toObject<ChimpagneEvent>()
          onSuccess(event)
        }
        .addOnFailureListener { onFailure(it) }
  }

  fun uploadEventPicture(
      event: ChimpagneEvent,
      onSuccess: (id: String) -> Unit,
      onFailure: (Exception) -> Unit,
      eventPictureUri: String
  ) {
    val imageRef = eventPictures.child(event.id)
    imageRef
        .putFile(eventPictureUri.toUri())
        .addOnSuccessListener {
          imageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
            onSuccess(downloadUrl.toString())
          }
        }
        .addOnFailureListener { onFailure(it) }
  }

  fun createEvent(
      event: ChimpagneEvent,
      onSuccess: (id: String) -> Unit,
      onFailure: (Exception) -> Unit,
      eventPictureUri: String? = null
  ) {
    if (database.accountManager.currentUserAccount == null) {
      onFailure(NotLoggedInException())
      return
    }
    val eventId = events.document().id
    if (eventPictureUri != null) {
      uploadEventPicture(
          event.copy(id = eventId),
          {
            updateEvent(
                event.copy(id = eventId, imageUri = it),
                {
                  database.accountManager.joinEvent(
                      eventId, ChimpagneRole.OWNER, { onSuccess(eventId) }, { onFailure(it) })
                },
                { onFailure(it) })
          },
          { onFailure(it) },
          eventPictureUri)
    } else {
      updateEvent(
          event.copy(id = eventId),
          {
            database.accountManager.joinEvent(
                eventId, ChimpagneRole.OWNER, { onSuccess(eventId) }, { onFailure(it) })
          },
          { onFailure(it) })
    }
  }

  fun updateEvent(
      event: ChimpagneEvent,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit,
      eventPictureUri: String? = null
  ) {

    if (event.id == "") {
      onFailure(Exception("null event id"))
      return
    }

    if (eventPictureUri != null && event.imageUri != eventPictureUri) {
      uploadEventPicture(
          event,
          {
            val eventWithPicture = event.copy(imageUri = it)
            events
                .document(event.id)
                .set(eventWithPicture)
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { onFailure(it) }
          },
          { onFailure(it) },
          eventPictureUri)
    } else {
      events
          .document(event.id)
          .set(event)
          .addOnSuccessListener { onSuccess() }
          .addOnFailureListener { onFailure(it) }
    }
  }

  fun deleteEvent(id: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    events
        .document(id)
        .get()
        .addOnSuccessListener { it ->
          val event = it.toObject<ChimpagneEvent>()
          if (event != null) {
            val users =
                listOf(event.ownerId) + event.staffs.keys.toList() + event.guests.keys.toList()
            users.forEach { userUID ->
              database.accountManager.atomic.leaveEvent(userUID, id, {}, {})
            }
          }
          events
              .document(id)
              .delete()
              .addOnSuccessListener { onSuccess() }
              .addOnFailureListener { onFailure(it) }
        }
        .addOnFailureListener(onFailure)
  }

  fun getEvents(
      listOfEventIDs: List<ChimpagneEventId>,
      onSuccess: (List<ChimpagneEvent>) -> Unit,
      onFailure: (Exception) -> Unit = {}
  ) {
    val tasks: MutableList<Task<QuerySnapshot>> = ArrayList()
    for (eventID in listOfEventIDs) {
      tasks.add(events.where(Filter.equalTo("id", eventID)).get())
    }

    // Collect all the query results together into a single list
    Tasks.whenAllComplete(tasks)
        .addOnCompleteListener {
          val events: MutableList<ChimpagneEvent> = ArrayList()
          for (task in tasks) {
            val snap = task.result
            for (doc in snap!!.documents) {
              val event = doc.toObject<ChimpagneEvent>()!!
              events.add(event)
            }
          }
          onSuccess(events)
        }
        .addOnFailureListener { onFailure(it) }
  }

  fun deleteAllRelatedEvents(
      userUID: String,
      onSuccess: () -> Unit = {},
      onFailure: (Exception) -> Unit = {}
  ) {
    val ownerQuery = events.whereEqualTo("ownerId", userUID).get()
    val guestQuery = events.whereEqualTo("guests.$userUID", true).get()
    val staffQuery = events.whereEqualTo("staffs.$userUID", true).get()

    Tasks.whenAllComplete(ownerQuery, guestQuery, staffQuery)
        .addOnCompleteListener { tasks ->
          if (tasks.isSuccessful) {
            val ownerDocs = ownerQuery.result?.documents ?: emptyList()
            val guestDocs = guestQuery.result?.documents ?: emptyList()
            val staffDocs = staffQuery.result?.documents ?: emptyList()

            val deleteTasks =
                ownerDocs.map { document -> events.document(document.id).delete() }.toMutableList()

            val updateTasks =
                (guestDocs + staffDocs).map { document ->
                  val event = document.toObject<ChimpagneEvent>()
                  val updatedGuests = event!!.guests.toMutableMap()
                  val updatedStaffs = event.staffs.toMutableMap()

                  updatedGuests.remove(userUID)
                  updatedStaffs.remove(userUID)

                  events
                      .document(document.id)
                      .update(mapOf("guests" to updatedGuests, "staffs" to updatedStaffs))
                }

            val allTasks = deleteTasks + updateTasks

            Tasks.whenAllComplete(allTasks)
                .addOnCompleteListener {
                  if (it.isSuccessful) {
                    onSuccess()
                  } else {
                    val exception = it.exception ?: Exception("Unknown error occurred")
                    onFailure(exception)
                  }
                }
                .addOnFailureListener { exception -> onFailure(exception) }
          } else {
            val exception = tasks.exception ?: Exception("Unknown error occurred")
            onFailure(exception)
          }
        }
        .addOnFailureListener { exception -> onFailure(exception) }
  }
}
