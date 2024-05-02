package com.monkeyteam.chimpagne.newtests

import android.net.Uri
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Firebase
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import com.monkeyteam.chimpagne.R
import com.monkeyteam.chimpagne.model.database.ChimpagneAccount
import com.monkeyteam.chimpagne.model.database.ChimpagneAccountUID
import com.monkeyteam.chimpagne.model.database.ChimpagneEvent
import com.monkeyteam.chimpagne.model.database.TEST_TABLES
import com.monkeyteam.chimpagne.model.location.Location
import com.monkeyteam.chimpagne.model.utils.buildTimestamp

val TEST_EVENTS =
    listOf(
        ChimpagneEvent(
            id = "FIRST_EVENT",
            title = "First event",
            description = "a random description",
            location = Location("Paris"),
            public = true,
            tags = listOf("vegan"),
            guests = emptyMap(),
            staffs = emptyMap(),
            startsAtTimestamp = buildTimestamp(9, 5, 2024, 0, 0),
            endsAtTimestamp = buildTimestamp(10, 5, 2024, 0, 0),
            ownerId = "PRINCE"),
        ChimpagneEvent(
            id = "SECOND_EVENT",
            title = "Second event",
            description = "I love bananas",
            location = Location("EPFL"),
            public = true,
            tags = listOf("bananas", "monkeys"),
            guests = emptyMap(),
            staffs = emptyMap(),
            startsAtTimestamp = buildTimestamp(1, 8, 2024, 0, 0),
            endsAtTimestamp = buildTimestamp(3, 9, 2024, 10, 0)),
        ChimpagneEvent(
            id = "THIRD_EVENT",
            title = "Third event",
            description = "Coucou",
            location = Location("Renens"),
            public = true,
            tags = listOf("students", "degen"),
            guests = mapOf(Pair("PRINCE", true)),
            staffs = emptyMap(),
            startsAtTimestamp = buildTimestamp(4, 2, 2025, 0, 0),
            endsAtTimestamp = buildTimestamp(4, 3, 2025, 0, 0)),
        ChimpagneEvent(
            id = "LOTR",
            title = "Watch LOTR",
            description = "The 2 towers",
            location = Location("EPFL, INM"),
            public = true,
            tags = listOf("lotr"),
            guests = emptyMap(),
            staffs = emptyMap(),
            startsAtTimestamp = buildTimestamp(1, 7, 2024, 0, 0),
            endsAtTimestamp = buildTimestamp(2, 7, 2024, 0, 0),
            ownerId = "PRINCE"))

val TEST_ACCOUNTS =
    listOf(
        ChimpagneAccount(
            firebaseAuthUID = "PRINCE",
            firstName = "Monkey",
            lastName = "Prince",
            location = Location("The jungle"),
            joinedEvents = mapOf(Pair("LOTR", true), Pair("THIRD_EVENT", true))),
        ChimpagneAccount(
            firebaseAuthUID = "JUAN",
            firstName = "Juan",
            lastName = "Litalien",
            location = Location("Italy"),
            joinedEvents = emptyMap()), /* THIS MUST BE EMPTY*/
        ChimpagneAccount(
            firebaseAuthUID = "THEREALKING",
            firstName = "Nat",
            lastName = "Lambert",
            location = Location("Waterloo"),
            joinedEvents = emptyMap()))

val TEST_PROFILE_PICTURES =
    mapOf<ChimpagneAccountUID, Uri>(
        "PRINCE" to
            Uri.parse(
                "android.resource://com.monkeyteam.chimpagne/" + R.drawable.chimpagne_app_logo),
        "JUAN" to
            Uri.parse(
                "android.resource://com.monkeyteam.chimpagne/" +
                    R.drawable.default_user_profile_picture)
        // DO NOT GIVE PROFILE PICTURE TO "THEREALKING"
        )

fun dropTable(table: CollectionReference) {
  val documents = Tasks.await(table.get())
  for (doc in documents) {
    Tasks.await(doc.reference.delete())
  }
}

fun initializeTestDatabase(
    events: List<ChimpagneEvent> = TEST_EVENTS,
    accounts: List<ChimpagneAccount> = TEST_ACCOUNTS,
    profilePictures: Map<ChimpagneAccountUID, Uri> = TEST_PROFILE_PICTURES
) {
  val eventsTable = Firebase.firestore.collection(TEST_TABLES.EVENTS)
  val accountsTable = Firebase.firestore.collection(TEST_TABLES.ACCOUNTS)
  val profilePicturesTable = Firebase.storage.reference.child(TEST_TABLES.PROFILE_PICTURES)

  dropTable(eventsTable)
  for (event in events) {
    Tasks.await(eventsTable.document(event.id).set(event))
  }

  dropTable(accountsTable)
  for (account in accounts) {
    Tasks.await(accountsTable.document(account.firebaseAuthUID).set(account))
  }

  //  Tasks.await(profilePicturesTable.delete().addOnFailureListener {  })
  for (entry in profilePictures.entries.iterator()) {
    Tasks.await(profilePicturesTable.child(entry.key).putFile(entry.value))
  }
}
