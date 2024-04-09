package com.monkeyteam.chimpagne

import AccountSettings
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.monkeyteam.chimpagne.ui.AccountEdit
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import com.monkeyteam.chimpagne.ui.theme.AccountCreation
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
      AccountCreation(navObject)
    }

    composeTestRule.onNodeWithTag("accountCreationLabel").assertTextContains("Créer votre compte")
    composeTestRule.onNodeWithTag("firstNameTextField").assertTextContains("Prénom")
    composeTestRule.onNodeWithTag("lastNameTextField").assertTextContains("Nom de famille")
    composeTestRule.onNodeWithTag("locationTextField").assertTextContains("Choisissez votre ville")

    composeTestRule.onNodeWithTag("changeLanguageSwitch").performClick()

    composeTestRule.onNodeWithTag("accountCreationLabel").assertTextContains("Create your Account")
    composeTestRule.onNodeWithTag("firstNameTextField").assertTextContains("First Name")
    composeTestRule.onNodeWithTag("lastNameTextField").assertTextContains("Last Name")
    composeTestRule.onNodeWithTag("locationTextField").assertTextContains("Choose your City")
  }

  @Test
  fun testTextInputWorks() {
    composeTestRule.setContent {
      val navObject = NavigationActions(rememberNavController())
      AccountCreation(navObject)
    }

    composeTestRule.onNodeWithTag("firstNameTextField").performTextInput("John")
    composeTestRule.onNodeWithTag("lastNameTextField").performTextInput("Doe")
    composeTestRule.onNodeWithTag("locationTextField").performTextInput("Paris")

    composeTestRule.onNodeWithTag("firstNameTextField").assertTextContains("John")
    composeTestRule.onNodeWithTag("lastNameTextField").assertTextContains("Doe")
    composeTestRule.onNodeWithTag("locationTextField").assertTextContains("Paris")
  }
}

@RunWith(AndroidJUnit4::class)
class AccountEditUITest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun testLanguageChangeWorks() {

    composeTestRule.setContent {
      val navObject = NavigationActions(rememberNavController())
      AccountEdit(navObject)
    }

    composeTestRule.onNodeWithTag("accountCreationLabel").assertTextContains("Edit Account")
    composeTestRule.onNodeWithTag("firstNameTextField").assertTextContains("First Name")
    composeTestRule.onNodeWithTag("lastNameTextField").assertTextContains("Last Name")
    composeTestRule.onNodeWithTag("locationTextField").assertTextContains("Choose your City")

    composeTestRule.onNodeWithTag("changeLanguageSwitch").performClick()

    composeTestRule
        .onNodeWithTag("accountCreationLabel")
        .assertTextContains("Modifier le compte") // To changes
    composeTestRule.onNodeWithTag("firstNameTextField").assertTextContains("Prénom")
    composeTestRule.onNodeWithTag("lastNameTextField").assertTextContains("Nom de famille")
    composeTestRule.onNodeWithTag("locationTextField").assertTextContains("Choisissez votre ville")
  }
}

@RunWith(AndroidJUnit4::class)
class AccountSettingsUITest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun testSettingsCorrect() {

    composeTestRule.setContent {
      val navObject = NavigationActions(rememberNavController())
      AccountSettings(navObject)
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
