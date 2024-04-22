package com.monkeyteam.chimpagne.accounts

import AccountSettings
import android.net.Uri
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.monkeyteam.chimpagne.model.database.ChimpagneAccount
import com.monkeyteam.chimpagne.model.database.ChimpagneAccountManager
import com.monkeyteam.chimpagne.model.database.Database
import com.monkeyteam.chimpagne.model.location.Location
import com.monkeyteam.chimpagne.ui.AccountEdit
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import com.monkeyteam.chimpagne.ui.theme.AccountCreation
import com.monkeyteam.chimpagne.viewmodels.AccountViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AccountCreationUITest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun testLanguageChangeWorks() {

    composeTestRule.setContent {
      val navObject = NavigationActions(rememberNavController())
      AccountCreation(navObject, AccountViewModel())
    }

    /*composeTestRule.onNodeWithTag("accountCreationLabel").assertTextContains("Créer votre compte")
    composeTestRule.onNodeWithTag("firstNameTextField").assertTextContains("Prénom")
    composeTestRule.onNodeWithTag("lastNameTextField").assertTextContains("Nom de famille")

    composeTestRule.onNodeWithTag("changeLanguageSwitch").performClick()*/

    composeTestRule.onNodeWithTag("accountCreationLabel").assertTextContains("Create Account")
    composeTestRule.onNodeWithTag("firstNameTextField").assertTextContains("First Name")
    composeTestRule.onNodeWithTag("lastNameTextField").assertTextContains("Last Name")
  }

  /* @Test
  fun testTextInputWorks() {
    composeTestRule.setContent {
      val navObject = NavigationActions(rememberNavController())
      AccountCreation(navObject, AccountViewModel("test@gmail.com"))
    }

    composeTestRule.onNodeWithTag("firstNameTextField").performTextInput("yy")
    composeTestRule.onNodeWithTag("lastNameTextField").performTextInput("ey")
    composeTestRule.onNodeWithTag("locationTextField").performTextInput("is")

    composeTestRule.onNodeWithTag("firstNameTextField").assertTextContains("Johnyy")
    composeTestRule.onNodeWithTag("lastNameTextField").assertTextContains("Doeey")
    composeTestRule.onNodeWithTag("locationTextField").assertTextContains("Paris")
  }*/
}

@RunWith(AndroidJUnit4::class)
class AccountEditUITest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun testLanguageChangeWorks() {

    composeTestRule.setContent {
      val navObject = NavigationActions(rememberNavController())
      AccountEdit(navObject, AccountViewModel())
    }

    composeTestRule.onNodeWithTag("accountCreationLabel").assertTextContains("Edit Account")
    composeTestRule.onNodeWithTag("firstNameTextField").assertTextContains("First Name")
    composeTestRule.onNodeWithTag("lastNameTextField").assertTextContains("Last Name")

    /*composeTestRule.onNodeWithTag("changeLanguageSwitch").performClick()

      composeTestRule
          .onNodeWithTag("accountCreationLabel")
          .assertTextContains("Modifier le compte") // To changes
      composeTestRule.onNodeWithTag("firstNameTextField").assertTextContains("Prénom")
      composeTestRule.onNodeWithTag("lastNameTextField").assertTextContains("Nom de famille")
      composeTestRule.onNodeWithTag("locationTextField").assertTextContains("Choisissez votre ville")
    */
  }
}

@RunWith(AndroidJUnit4::class)
class AccountSettingsUITest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun testSettingsCorrect() {

    composeTestRule.setContent {
      val navObject = NavigationActions(rememberNavController())
      AccountSettings(navObject, AccountViewModel())
    }

    val preferredLanguageIsEnglish = true

    if (preferredLanguageIsEnglish) {
      composeTestRule.onNodeWithTag("firstNameTextField").assertTextContains("First Name")
      composeTestRule.onNodeWithTag("lastNameTextField").assertTextContains("Last Name")
      composeTestRule.onNodeWithTag("locationTextField").assertTextContains("Location")
    } else {
      composeTestRule.onNodeWithTag("firstNameTextField").assertTextContains("Prénom")
      composeTestRule.onNodeWithTag("lastNameTextField").assertTextContains("Nom de famille")
      composeTestRule.onNodeWithTag("locationTextField").assertTextContains("Ville")
    }
  }
}

@RunWith(AndroidJUnit4::class)
class TestAccountViewModel {


  private val accountManager: ChimpagneAccountManager = Database.instance.accountManager
  @Before
  fun signIn() {
    accountManager.signInTo(ChimpagneAccount())
  }

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun TestVMSetterFunctions() {
    val accountViewModel = AccountViewModel(accountManager)

    assert(accountViewModel.uiState.value.tempAccount.firstName == "")
    accountViewModel.updateFirstName("John")
    assert(accountViewModel.uiState.value.tempAccount.firstName == "John")

    assert(accountViewModel.uiState.value.tempAccount.lastName == "")
    accountViewModel.updateLastName("Doe")
    assert(accountViewModel.uiState.value.tempAccount.lastName == "Doe")

    assert(accountViewModel.uiState.value.tempAccount.location == Location())
    accountViewModel.updateLocation(Location("Paris", 48.8566, 2.3522))
    assert(accountViewModel.uiState.value.tempAccount.location == Location("Paris", 48.8566, 2.3522))

    assert(accountViewModel.uiState.value.tempProfilePicture == null)
    accountViewModel.updateProfilePicture(Uri.parse("https://www.google.com"))
    assert(accountViewModel.uiState.value.tempProfilePicture == Uri.parse("https://www.google.com"))
  }

  @Test
  fun TestVMSubmitUpdatedAccountWorks() {
    val accountViewModel = AccountViewModel(accountManager)
    accountViewModel.updateFirstName("John")
    accountViewModel.updateLastName("Doe")
    accountViewModel.updateLocation(Location("Paris", 48.8566, 2.3522))
    accountViewModel.updateProfilePicture(Uri.parse("https://www.google.com"))

    accountViewModel.submitUpdatedAccount({
      assert(accountViewModel.uiState.value.currentUserAccount?.firstName  == "Joe")
      assert(accountViewModel.uiState.value.currentUserAccount?.lastName  == "Doe")
      assert(accountViewModel.uiState.value.currentUserAccount?.location  == Location("Paris", 48.8566, 2.3522))
      assert(accountViewModel.uiState.value.currentUserProfilePicture  == Uri.parse("https://www.google.com"))
    }, {
      assert(accountViewModel.uiState.value.currentUserUID == null)
      assert("Invalid account UID" == it.message)
    })
  }

  @Test
  fun TestVMSubmitUpdatedNullUID(){

  }
}


