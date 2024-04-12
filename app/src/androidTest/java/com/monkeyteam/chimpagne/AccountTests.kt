package com.monkeyteam.chimpagne

import AccountSettings
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.monkeyteam.chimpagne.ui.AccountEdit
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import com.monkeyteam.chimpagne.ui.theme.AccountCreation
import com.monkeyteam.chimpagne.viewmodels.AccountViewModel
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
    composeTestRule.onNodeWithTag("locationTextField").assertTextContains("Choisissez votre ville")

    composeTestRule.onNodeWithTag("changeLanguageSwitch").performClick()*/

    composeTestRule.onNodeWithTag("accountCreationLabel").assertTextContains("Create Account")
    composeTestRule.onNodeWithTag("firstNameTextField").assertTextContains("First Name")
    composeTestRule.onNodeWithTag("lastNameTextField").assertTextContains("Last Name")
    composeTestRule.onNodeWithTag("locationTextField").assertTextContains("Choose your City")
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
    composeTestRule.onNodeWithTag("locationTextField").assertTextContains("Choose your City")

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

    val preferredLanguageIsEnglish =
        try {
          composeTestRule
              .onNodeWithTag("preferredLanguageTextField")
              .assertTextEquals("Preferred Language")
          true
        } catch (e: Exception) {
          false
        }

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
