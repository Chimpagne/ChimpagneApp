package com.monkeyteam.chimpagne.newtests.model.account

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.monkeyteam.chimpagne.model.database.ChimpagneAccount
import com.monkeyteam.chimpagne.model.database.ChimpagneAccountUID
import com.monkeyteam.chimpagne.model.database.Database
import com.monkeyteam.chimpagne.model.utils.NetworkNotAvailableException
import com.monkeyteam.chimpagne.newtests.TEST_ACCOUNTS
import com.monkeyteam.chimpagne.newtests.initializeTestDatabase
import com.monkeyteam.chimpagne.newtests.setMobileDataEnabled
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Before
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
    accountManager.updateCurrentAccount(ChimpagneAccount("BANANA"), onSuccess = { assertTrue(false) }, onFailure = {
      exception = it
      loading = false })
    while (loading) {}

    assertEquals(NetworkNotAvailableException::class, exception::class)
  }

  @Test
  fun leaveEventIsDisabled() {
    var loading = true
    var exception: Exception = Exception()
    accountManager.atomic.leaveEvent("MONKEY", "BANANA", onSuccess = { assertTrue(false) }, onFailure = {
      exception = it
      loading = false })
    while (loading) {}
    assertEquals(NetworkNotAvailableException::class, exception::class)
  }
}
