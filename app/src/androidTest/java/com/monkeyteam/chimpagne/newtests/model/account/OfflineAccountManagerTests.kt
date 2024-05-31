package com.monkeyteam.chimpagne.newtests.model.account

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.monkeyteam.chimpagne.model.database.ChimpagneAccount
import com.monkeyteam.chimpagne.model.database.Database
import com.monkeyteam.chimpagne.model.utils.NetworkNotAvailableException
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OfflineAccountManagerTests {

  val database = Database(allowInternetAccess = false)
  val accountManager = database.accountManager

  @Test
  fun accountUpdateIsDisabled() {
    var loading = true
    var exception: Exception = Exception()
    accountManager.updateCurrentAccount(
        ChimpagneAccount("BANANA"),
        onSuccess = { assertTrue(false) },
        onFailure = {
          exception = it
          loading = false
        })
    while (loading) {}

    assertEquals(NetworkNotAvailableException::class, exception::class)
  }

  @Test
  fun leaveEventIsDisabled() {
    var loading = true
    var exception: Exception = Exception()
    accountManager.atomic.leaveEvent(
        "MONKEY",
        "BANANA",
        onSuccess = { assertTrue(false) },
        onFailure = {
          exception = it
          loading = false
        })
    while (loading) {}
    assertEquals(NetworkNotAvailableException::class, exception::class)
  }
}
