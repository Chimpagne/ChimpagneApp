package com.monkeyteam.chimpagne

import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import com.monkeyteam.chimpagne.model.database.ChimpagneAccount
import com.monkeyteam.chimpagne.model.database.ChimpagneAccountManager
import com.monkeyteam.chimpagne.model.location.Location
import com.monkeyteam.chimpagne.viewmodels.AccountViewModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AccountSystemTests {
  val accounts = Firebase.firestore.collection("testAccounts")
  val profilePictures = Firebase.storage.reference.child("testAccounts")

  private val accountManager: ChimpagneAccountManager = ChimpagneAccountManager(
    accounts,
    profilePictures
  )

  private val testAccount1 = ChimpagneAccount(
    firebaseAuthUID = "ILOVEBANANAS",
    firstName = "Monkey",
    lastName = "Prince",
    location = Location(name = "The monkeys' jungle")
  )

  private val testAccount2 = ChimpagneAccount(
    firebaseAuthUID = "JOJO",
    firstName = "Manu",
    lastName = "Jojo",
    location = Location(name = "The monkeys' jungle")
  )

  @Before
  fun signIn() {
    var count = 3
    accounts.get().addOnSuccessListener { documents ->
      for (doc in documents) {
        doc.reference.delete()
      }
    }.addOnCompleteListener {
      accounts.document(testAccount1.firebaseAuthUID).set(testAccount1).addOnCompleteListener { count-- }
      accounts.document(testAccount2.firebaseAuthUID).set(testAccount2).addOnCompleteListener { count-- }
    }
    profilePictures.delete().addOnCompleteListener {

      val uri = Uri.parse("android.resource://com.monkeyteam.chimpagne/" + R.drawable.chimpagne_app_logo)
        profilePictures.child(testAccount1.firebaseAuthUID).putFile(uri).addOnCompleteListener { count-- }
    }

    while (count > 0) {}
  }


  @Test
  fun testLogin() {
    val accountViewModel = AccountViewModel(accountManager = accountManager)

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
  fun testAccountEdition() {
    val accountViewModel = AccountViewModel(accountManager = accountManager)

    accountViewModel.loginToChimpagneAccount(testAccount1.firebaseAuthUID, {}, {})
    while (accountViewModel.uiState.value.loading) {}

    assertEquals(testAccount1, accountViewModel.uiState.value.currentUserAccount)
    assertEquals(ChimpagneAccount(), accountViewModel.uiState.value.tempAccount)
    accountViewModel.copyRealToTemp()
    assertEquals(testAccount1, accountViewModel.uiState.value.tempAccount)

    accountViewModel.updateFirstName("Quiche")
    accountViewModel.updateLastName("Lorraine")
    accountViewModel.updateLocation(Location("USA"))
    assertEquals(testAccount1, accountViewModel.uiState.value.currentUserAccount)
    assertEquals(testAccount1.copy(firstName = "Quiche", lastName = "Lorraine", location = Location("USA")), accountViewModel.uiState.value.tempAccount)
    accountViewModel.submitUpdatedAccount({}, {})
    while (accountViewModel.uiState.value.loading) {}
    assertEquals(testAccount1.copy(firstName = "Quiche", lastName = "Lorraine", location = Location("USA")), accountViewModel.uiState.value.currentUserAccount)

  }
}
