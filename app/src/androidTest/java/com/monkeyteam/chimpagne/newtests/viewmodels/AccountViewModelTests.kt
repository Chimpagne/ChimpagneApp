package com.monkeyteam.chimpagne.newtests.viewmodels

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.monkeyteam.chimpagne.model.database.ChimpagneAccount
import com.monkeyteam.chimpagne.model.database.Database
import com.monkeyteam.chimpagne.model.location.Location
import com.monkeyteam.chimpagne.newtests.TEST_ACCOUNTS
import com.monkeyteam.chimpagne.newtests.initializeTestDatabase
import com.monkeyteam.chimpagne.viewmodels.AccountViewModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AccountViewModelTests {

  val database = Database()
  private val accountManager = database.accountManager

  private val testAccount1 = TEST_ACCOUNTS[0]
  private val testAccount2 = TEST_ACCOUNTS[2]

  @Before
  fun initTests() {
    initializeTestDatabase()
  }

  @Test
  fun testLogin() {
    val accountViewModel = AccountViewModel(database = database)

    assertEquals(null, accountViewModel.uiState.value.currentUserAccount)
    assertEquals(null, accountViewModel.uiState.value.currentUserProfilePicture)
    assertEquals(null, accountViewModel.uiState.value.currentUserUID)
    assertEquals(ChimpagneAccount(), accountViewModel.uiState.value.tempAccount)
    assertEquals(null, accountViewModel.uiState.value.tempProfilePicture)

    accountViewModel.loginToChimpagneAccount("banana", {}, {})
    while (accountViewModel.uiState.value.loading) {}

    assertEquals(null, accountManager.currentUserAccount)
    assertEquals(null, accountViewModel.uiState.value.currentUserAccount)
    assertEquals(null, accountViewModel.uiState.value.currentUserProfilePicture)
    assertEquals("banana", accountViewModel.uiState.value.currentUserUID)

    accountViewModel.logoutFromChimpagneAccount()
    while (accountViewModel.uiState.value.loading) {}

    assertEquals(null, accountViewModel.uiState.value.currentUserAccount)
    assertEquals(null, accountViewModel.uiState.value.currentUserProfilePicture)
    assertEquals(null, accountViewModel.uiState.value.currentUserUID)

    accountViewModel.loginToChimpagneAccount(testAccount1.firebaseAuthUID, {}, {})
    while (accountViewModel.uiState.value.loading) {}

    assertEquals(testAccount1, accountViewModel.uiState.value.currentUserAccount)
    assertNotNull(accountViewModel.uiState.value.currentUserProfilePicture)
    assertEquals(testAccount1.firebaseAuthUID, accountViewModel.uiState.value.currentUserUID)

    accountViewModel.loginToChimpagneAccount(testAccount2.firebaseAuthUID, {}, {})
    while (accountViewModel.uiState.value.loading) {}

    assertEquals(testAccount2, accountViewModel.uiState.value.currentUserAccount)
    assertEquals(null, accountViewModel.uiState.value.currentUserProfilePicture)
    assertEquals(testAccount2.firebaseAuthUID, accountViewModel.uiState.value.currentUserUID)
  }

  @Test
  fun testUpdateAccountDetails() {
    val accountViewModel = AccountViewModel(database = database)
    accountViewModel.loginToChimpagneAccount(testAccount1.firebaseAuthUID, {}, {})
    while (accountViewModel.uiState.value.loading) {}

    val newFirstName = "UpdatedFirstName"
    val newLastName = "UpdatedLastName"
    val newLocation = Location("UpdatedLocation")
    accountViewModel.updateFirstName(newFirstName)
    accountViewModel.updateLastName(newLastName)
    accountViewModel.updateLocation(newLocation)

    accountViewModel.submitUpdatedAccount({}, {})
    while (accountViewModel.uiState.value.loading) {}

    assertEquals(newFirstName, accountViewModel.uiState.value.currentUserAccount?.firstName)
    assertEquals(newLastName, accountViewModel.uiState.value.currentUserAccount?.lastName)
    assertEquals(newLocation, accountViewModel.uiState.value.currentUserAccount?.location)
  }

  @Test
  fun testIsUserLoggedIn() {
    val accountViewModel = AccountViewModel(database = database)
    assertFalse(accountViewModel.isUserLoggedIn())
  }

  @Test
  fun fetchAccountsTest() {
    val accounts = TEST_ACCOUNTS.associate { (it.firebaseAuthUID to it) }

    val accountViewModel = AccountViewModel(database = database)
    accountViewModel.fetchAccounts(accounts.keys.toList())
    while (accountViewModel.uiState.value.loading) {}

    assertEquals(accounts, accountViewModel.uiState.value.fetchedAccounts)
  }
}
