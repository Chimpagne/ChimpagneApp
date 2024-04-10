package com.monkeyteam.chimpagne

import android.util.Log
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.Timestamp
import com.monkeyteam.chimpagne.model.database.ChimpagneEvent
import com.monkeyteam.chimpagne.model.location.Location
import com.monkeyteam.chimpagne.model.viewmodels.EventViewModel
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Calendar
import java.util.Date

@RunWith(AndroidJUnit4::class)
class EventViewModelTests {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun TestVMSetterGetterFunctions() {
        val startCalendarDate = Calendar.getInstance()
        startCalendarDate.set(2024, 5, 9, 0, 0, 0)

        val endCalendarDate = Calendar.getInstance()
        endCalendarDate.set(2024, 5, 10, 0, 0, 0)

        val testEvent = ChimpagneEvent(
            "0",
            "SWENT",
            "swent party",
            Location("EPFL", 46.518659400000004,6.566561505148001),
            true,
            listOf("vegan", "wild"),
            emptyMap(),
            Timestamp(startCalendarDate.time),
            Timestamp(endCalendarDate.time)
        )

        val eventVM = EventViewModel()

        eventVM.updateEventTitle(testEvent.title)
        assertTrue(eventVM.getEventTitle() == testEvent.title)

        eventVM.updateEventDescription(testEvent.description)
        assertTrue(eventVM.getEventDescription() == testEvent.description)

        eventVM.updateEventLocationSearchField(testEvent.location.name)
        assertTrue(eventVM.getEventLocationSearchField() == testEvent.location.name)

        eventVM.updateEventLocation(testEvent.location)
        assertTrue(eventVM.getEventLocation().name == testEvent.location.name)
        assertTrue(eventVM.getEventLocation().latitude == testEvent.location.latitude)
        assertTrue(eventVM.getEventLocation().longitude == testEvent.location.longitude)
        assertTrue(eventVM.getEventLocation().geohash == testEvent.location.geohash)

        eventVM.updateEventPublicity(testEvent.isPublic)
        assertTrue(eventVM.getEventPublicity() == testEvent.isPublic)

        eventVM.updateEventTags(testEvent.tags)
        assertTrue(eventVM.getEventTags().size == testEvent.tags.size)
        assertTrue(eventVM.getEventTags().toSet() == testEvent.tags.toSet())

        eventVM.updateEventStartCalendarDate(testEvent.startAt)
        assertTrue(eventVM.getEventStartCalendarDate() == testEvent.startAt)

        eventVM.updateEventEndCalendarDate(testEvent.endsAt)
        assertTrue(eventVM.getEventEndCalendarDate() == testEvent.endsAt)
    }

    @Test
    fun TestCreateSearchDeleteAnEvent(){
        val startCalendarDate = Calendar.getInstance()
        startCalendarDate.set(2024, 5, 9, 0, 0, 0)

        val endCalendarDate = Calendar.getInstance()
        endCalendarDate.set(2024, 5, 10, 0, 0, 0)

        val testEvent = ChimpagneEvent(
            "",
            "SWENT",
            "swent party",
            Location("EPFL", 46.518659400000004,6.566561505148001),
            true,
            listOf("vegan", "wild"),
            emptyMap(),
            Timestamp(startCalendarDate.time),
            Timestamp(endCalendarDate.time)
        )

        val eventCreationVM = EventViewModel()

        eventCreationVM.updateEventTitle(testEvent.title)
        eventCreationVM.updateEventDescription(testEvent.description)
        eventCreationVM.updateEventLocationSearchField(testEvent.location.name)
        eventCreationVM.updateEventLocation(testEvent.location)
        eventCreationVM.updateEventPublicity(testEvent.isPublic)
        eventCreationVM.updateEventTags(testEvent.tags)
        eventCreationVM.updateEventStartCalendarDate(testEvent.startAt)
        eventCreationVM.updateEventEndCalendarDate(testEvent.endsAt)

        eventCreationVM.createTheEvent(onSuccess = {assertTrue(true)}, onFailure = {assertTrue(false)})

        //Wait for database to get the data
        runBlocking { delay(5000) }

        val eventID = eventCreationVM.getEventId()

        val eventSearchVM = EventViewModel(eventID = eventID, onSuccess = {assertTrue(true)}, onFailure = {assertTrue(false)})

        //Wait for database to get the data
        runBlocking { delay(5000) }

        assertTrue(eventSearchVM.getEventTitle() == testEvent.title)
        assertTrue(eventSearchVM.getEventDescription() == testEvent.description)
        assertTrue(eventSearchVM.getEventLocationSearchField() == testEvent.location.name)
        assertTrue(eventSearchVM.getEventLocation().name == testEvent.location.name)
        assertTrue(eventSearchVM.getEventLocation().latitude == testEvent.location.latitude)
        assertTrue(eventSearchVM.getEventLocation().longitude == testEvent.location.longitude)
        assertTrue(eventSearchVM.getEventLocation().geohash == testEvent.location.geohash)
        //assertTrue(eventSearchVM.getEventPublicity() == testEvent.isPublic)//TODO
        assertTrue(eventSearchVM.getEventTags().size == testEvent.tags.size)
        assertTrue(eventSearchVM.getEventTags().toSet() == testEvent.tags.toSet())

        //Log.d("WHAT IS ON THE VM", eventSearchVM.getEventStartCalendarDate().toString())
        //Log.d("WHAT IS ON THE EVENT", testEvent.startAt.toString())

        //assertTrue(eventSearchVM.getEventStartCalendarDate() == testEvent.startAt)//TODO
        //assertTrue(eventSearchVM.getEventEndCalendarDate() == testEvent.endsAt)


        eventSearchVM.deleteTheEvent(onSuccess = {assertTrue(true)}, onFailure = {assertTrue(false)})

        //Wait for database to get the data
        runBlocking { delay(5000) }

        assertTrue(eventSearchVM.getEventId() == "")
    }

    @Test
    fun TestUpdateAnEvent(){
        val startCalendarDate = Calendar.getInstance()
        startCalendarDate.set(2024, 3, 28, 6, 13, 29)

        val endCalendarDate = Calendar.getInstance()
        endCalendarDate.set(2024, 3, 28, 6, 13, 29)


        val testEvent = ChimpagneEvent(
            "",
            "",
            "Default Description",
            Location("Paris", 48.8534951,2.3483915),
            true,
            emptyList(),
            emptyMap(),
            Timestamp(startCalendarDate.time),
            Timestamp(endCalendarDate.time)
        )

        val startUpdatedCalendarDate = Calendar.getInstance()
        startCalendarDate.set(2025, 4, 29, 7, 14, 30)

        val endUpdatedCalendarDate = Calendar.getInstance()
        endCalendarDate.set(2025, 4, 29, 7, 14, 30)


        val testUpdatedEvent = ChimpagneEvent(
            "",
            "London",
            "Harry Potter",
            Location("United Kingdown", 38.8534951,12.3483915),
            false,
            listOf("magic", "wands"),
            emptyMap(),
            Timestamp(startUpdatedCalendarDate.time),
            Timestamp(endUpdatedCalendarDate.time)
        )

        val eventCreationVM = EventViewModel()

        eventCreationVM.updateEventTitle(testEvent.title)
        eventCreationVM.updateEventDescription(testEvent.description)
        eventCreationVM.updateEventLocationSearchField(testEvent.location.name)
        eventCreationVM.updateEventLocation(testEvent.location)
        eventCreationVM.updateEventPublicity(testEvent.isPublic)
        eventCreationVM.updateEventTags(testEvent.tags)
        eventCreationVM.updateEventStartCalendarDate(testEvent.startAt)
        eventCreationVM.updateEventEndCalendarDate(testEvent.endsAt)

        eventCreationVM.createTheEvent(onSuccess = {assertTrue(true)}, onFailure = {assertTrue(false)})

        //Wait for database to get the data
        runBlocking { delay(5000) }

        val eventID = eventCreationVM.getEventId()

        val eventSearchVM = EventViewModel(eventID = eventID, onSuccess = {assertTrue(true)}, onFailure = {assertTrue(false)})

        //Wait for database to get the data
        runBlocking { delay(5000) }

        assertTrue(eventSearchVM.getEventId() == eventID)

        eventSearchVM.updateEventTitle(testUpdatedEvent.title)
        eventSearchVM.updateEventDescription(testUpdatedEvent.description)
        eventSearchVM.updateEventLocationSearchField(testUpdatedEvent.location.name)
        eventSearchVM.updateEventLocation(testUpdatedEvent.location)
        eventSearchVM.updateEventPublicity(testUpdatedEvent.isPublic)
        eventSearchVM.updateEventTags(testUpdatedEvent.tags)
        eventSearchVM.updateEventStartCalendarDate(testUpdatedEvent.startAt)
        eventSearchVM.updateEventEndCalendarDate(testUpdatedEvent.endsAt)

        eventSearchVM.updateTheEvent(onSuccess = {assertTrue(true)}, onFailure = {assertTrue(false)})

        //Wait for database to get the data
        runBlocking { delay(5000) }

        val eventSearch2VM = EventViewModel(eventID = eventID, onSuccess = {assertTrue(true)}, onFailure = {assertTrue(false)})

        //Wait for database to get the data
        runBlocking { delay(5000) }

        assertTrue(eventSearch2VM.getEventTitle() == testUpdatedEvent.title)
        assertTrue(eventSearch2VM.getEventDescription() == testUpdatedEvent.description)
        assertTrue(eventSearch2VM.getEventLocationSearchField() == testUpdatedEvent.location.name)
        assertTrue(eventSearch2VM.getEventLocation().name == testUpdatedEvent.location.name)
        assertTrue(eventSearch2VM.getEventLocation().latitude == testUpdatedEvent.location.latitude)
        assertTrue(eventSearch2VM.getEventLocation().longitude == testUpdatedEvent.location.longitude)
        assertTrue(eventSearch2VM.getEventLocation().geohash == testUpdatedEvent.location.geohash)
        //assertTrue(eventSearch2VM.getEventPublicity() == testUpdatedEvent.isPublic) //TODO
        assertTrue(eventSearch2VM.getEventTags().size == testUpdatedEvent.tags.size)
        assertTrue(eventSearch2VM.getEventTags().toSet() == testUpdatedEvent.tags.toSet())
        //assertTrue(eventSearch2VM.getEventStartCalendarDate() == testUpdatedEvent.startAt) //TODO
        //assertTrue(eventSearch2VM.getEventEndCalendarDate() == testUpdatedEvent.endsAt)

        eventSearch2VM.deleteTheEvent(onSuccess = {assertTrue(true)}, onFailure = {assertTrue(false)})
    }

    @Test
    fun TestAddAndRemoveGuestsFromAnEvent(){
        val eventCreationVM = EventViewModel()

        eventCreationVM.createTheEvent(onSuccess = {assertTrue(true)}, onFailure = {assertTrue(false)})

        //Wait for database to get the data
        runBlocking { delay(5000) }

        val eventID = eventCreationVM.getEventId()

        val eventSearchVM = EventViewModel(eventID = eventID, onSuccess = {assertTrue(true)}, onFailure = {assertTrue(false)})

        //Wait for database to get the data
        runBlocking { delay(5000) }

        assertTrue(eventSearchVM.getEventId() == eventID)

        eventSearchVM.addGuestToTheEvent("Clement", onSuccess = {assertTrue(true)}, onFailure = {assertTrue(false)})
        eventSearchVM.addGuestToTheEvent("Lea", onSuccess = {assertTrue(true)}, onFailure = {assertTrue(false)})
        eventSearchVM.addGuestToTheEvent("Arnaud", onSuccess = {assertTrue(true)}, onFailure = {assertTrue(false)})

        //Wait for database to get the data
        runBlocking { delay(5000) }

        val guestSet = setOf("Clement", "Lea", "Arnaud")

        assertTrue(eventSearchVM.getEventGuestSet().size == guestSet.size)
        assertTrue(eventSearchVM.getEventGuestSet() == guestSet)

        eventSearchVM.removeGuestFromTheEvent("Clement", onSuccess = {assertTrue(true)}, onFailure = {assertTrue(false)})
        eventSearchVM.removeGuestFromTheEvent("Lea", onSuccess = {assertTrue(true)}, onFailure = {assertTrue(false)})
        eventSearchVM.removeGuestFromTheEvent("Arnaud", onSuccess = {assertTrue(true)}, onFailure = {assertTrue(false)})

        //Wait for database to get the data
        runBlocking { delay(5000) }

        assertTrue(eventSearchVM.getEventGuestSet().isEmpty())

        eventSearchVM.deleteTheEvent(onSuccess = {assertTrue(true)}, onFailure = {assertTrue(false)})
    }
}