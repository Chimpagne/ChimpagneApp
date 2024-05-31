package com.monkeyteam.chimpagne.newtests.model.account

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.monkeyteam.chimpagne.model.database.ChimpagneAccount
import com.monkeyteam.chimpagne.model.database.ChimpagneAccountUID
import com.monkeyteam.chimpagne.model.database.Database
import com.monkeyteam.chimpagne.newtests.TEST_ACCOUNTS
import com.monkeyteam.chimpagne.newtests.initializeTestDatabase
import com.monkeyteam.chimpagne.newtests.setMobileDataEnabled
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AccountManagerTests {

  val database = Database()

  lateinit var instrumentationContext: Context

  @Before
  fun init() {
    initializeTestDatabase()
    instrumentationContext = InstrumentationRegistry.getInstrumentation().context
  }

  @Test
  fun getMultipleAccountsTest() {
    var loading = true
    var result: Map<ChimpagneAccountUID, ChimpagneAccount?> = hashMapOf()
    database.accountManager.getAccounts(
        listOf("JUAN", "PRINCE", "fevoihegijogegjoiejgoi"),
        {
          result = it
          loading = false
        }) {
          assertTrue(false)
        }
    while (loading) {}

    assertEquals(TEST_ACCOUNTS[0], result["PRINCE"])
    assertEquals(TEST_ACCOUNTS[1], result["JUAN"])
    assertEquals(null, result["fevoihegijogegjoiejgoi"])
  }

  @Test
  fun updateAccountWhileOfflineIsDisabled() {
    setMobileDataEnabled(instrumentationContext, false)

  }
}
