package com.monkeyteam.chimpagne.newtests.ui.account

import AccountSettings
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.monkeyteam.chimpagne.model.database.Database
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import com.monkeyteam.chimpagne.viewmodels.AccountViewModel
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AccountSettingsUITest {

  val database = Database()

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun testSettingsCorrect() {

    composeTestRule.setContent {
      val navObject = NavigationActions(rememberNavController())
      AccountSettings(navObject, AccountViewModel(database = database))
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
