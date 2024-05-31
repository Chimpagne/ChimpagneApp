package com.monkeyteam.chimpagne.newtests.model.event

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.monkeyteam.chimpagne.model.database.ChimpagneEvent
import com.monkeyteam.chimpagne.model.database.ChimpagneSupply
import com.monkeyteam.chimpagne.model.database.Database
import com.monkeyteam.chimpagne.model.utils.NetworkNotAvailableException
import com.monkeyteam.chimpagne.newtests.initializeTestDatabase
import junit.framework.TestCase
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OfflineEventManagerTests {

  val database = Database(allowInternetAccess = false)
  val eventManager = database.eventManager

  @Test
  fun uploadEventPictureIsDisabled() {
    var loading = true
    var exception: Exception = Exception()
    eventManager.uploadEventPicture(ChimpagneEvent("banana"), eventPictureUri = "MONKEY.PNG",  onSuccess = {
      assertTrue(false)
    }, onFailure = {
      exception = it
      loading = false
    })
    while (loading) {}
    TestCase.assertEquals(NetworkNotAvailableException::class, exception::class)
  }

  @Test
  fun createEventIsDisabled() {
    var loading = true
    var exception: Exception = Exception()
    eventManager.createEvent(ChimpagneEvent("banana"), eventPictureUri = "MONKEY.PNG",  onSuccess = {
      assertTrue(false)
    }, onFailure = {
      exception = it
      loading = false
    })
    while (loading) {}
    TestCase.assertEquals(NetworkNotAvailableException::class, exception::class)
  }

  @Test
  fun updateEventIsDisabled() {
    var loading = true
    var exception: Exception = Exception()
    eventManager.updateEvent(ChimpagneEvent("banana"), eventPictureUri = "MONKEY.PNG",  onSuccess = {
      assertTrue(false)
    }, onFailure = {
      exception = it
      loading = false
    })
    while (loading) {}
    TestCase.assertEquals(NetworkNotAvailableException::class, exception::class)
  }

  @Test
  fun deleteEventIsDisabled() {
    var loading = true
    var exception: Exception = Exception()
    eventManager.deleteEvent("BANANA",  onSuccess = {
      assertTrue(false)
    }, onFailure = {
      exception = it
      loading = false
    })
    while (loading) {}
    TestCase.assertEquals(NetworkNotAvailableException::class, exception::class)
  }

  @Test
  fun addGuestIsDisabled() {
    var loading = true
    var exception: Exception = Exception()
    eventManager.atomic.addGuest("BANANA", "MONKEY", onSuccess = {
      assertTrue(false)
    }, onFailure = {
      exception = it
      loading = false
    })
    while (loading) {}
    TestCase.assertEquals(NetworkNotAvailableException::class, exception::class)
  }

  @Test
  fun removeGuestIsDisabled() {
    var loading = true
    var exception: Exception = Exception()
    eventManager.atomic.removeGuest("BANANA", "MONKEY", onSuccess = {
      assertTrue(false)
    }, onFailure = {
      exception = it
      loading = false
    })
    while (loading) {}
    TestCase.assertEquals(NetworkNotAvailableException::class, exception::class)
  }

  @Test
  fun addStaffIsDisabled() {
    var loading = true
    var exception: Exception = Exception()
    eventManager.atomic.addStaff("BANANA", "MONKEY", onSuccess = {
      assertTrue(false)
    }, onFailure = {
      exception = it
      loading = false
    })
    while (loading) {}
    TestCase.assertEquals(NetworkNotAvailableException::class, exception::class)
  }

  @Test
  fun removeStaffIsDisabled() {
    var loading = true
    var exception: Exception = Exception()
    eventManager.atomic.removeGuest("BANANA", "MONKEY", onSuccess = {
      assertTrue(false)
    }, onFailure = {
      exception = it
      loading = false
    })
    while (loading) {}
    TestCase.assertEquals(NetworkNotAvailableException::class, exception::class)
  }

  @Test
  fun updateSupplyIsDisabled() {
    var loading = true
    var exception: Exception = Exception()
    eventManager.atomic.updateSupply("BANANA", ChimpagneSupply("MONKEY"), onSuccess = {
      assertTrue(false)
    }, onFailure = {
      exception = it
      loading = false
    })
    while (loading) {}
    TestCase.assertEquals(NetworkNotAvailableException::class, exception::class)
  }

  @Test
  fun removeSupplyIsDisabled() {
    var loading = true
    var exception: Exception = Exception()
    eventManager.atomic.removeSupply("BANANA", "MONKEY", onSuccess = {
      assertTrue(false)
    }, onFailure = {
      exception = it
      loading = false
    })
    while (loading) {}
    TestCase.assertEquals(NetworkNotAvailableException::class, exception::class)
  }

  @Test
  fun assignSupplyIsDisabled() {
    var loading = true
    var exception: Exception = Exception()
    eventManager.atomic.assignSupply("PARTY", "BANANA", "MONKEY", onSuccess = {
      assertTrue(false)
    }, onFailure = {
      exception = it
      loading = false
    })
    while (loading) {}
    TestCase.assertEquals(NetworkNotAvailableException::class, exception::class)
  }

  @Test
  fun unassignSupplyIsDisabled() {
    var loading = true
    var exception: Exception = Exception()
    eventManager.atomic.unassignSupply("PARTY", "BANANA", "MONKEY", onSuccess = {
      assertTrue(false)
    }, onFailure = {
      exception = it
      loading = false
    })
    while (loading) {}
    TestCase.assertEquals(NetworkNotAvailableException::class, exception::class)
  }

  @Test
  fun deletePollIsDisabled() {
    var loading = true
    var exception: Exception = Exception()
    eventManager.atomic.deletePoll("PARTY", "BANANA", onSuccess = {
      assertTrue(false)
    }, onFailure = {
      exception = it
      loading = false
    })
    while (loading) {}
    TestCase.assertEquals(NetworkNotAvailableException::class, exception::class)
  }

  @Test
  fun castPollVoteIsDisabled() {
    var loading = true
    var exception: Exception = Exception()
    eventManager.atomic.castPollVote("PARTY", "POLL","BANANA", 0, onSuccess = {
      assertTrue(false)
    }, onFailure = {
      exception = it
      loading = false
    })
    while (loading) {}
    TestCase.assertEquals(NetworkNotAvailableException::class, exception::class)
  }
}