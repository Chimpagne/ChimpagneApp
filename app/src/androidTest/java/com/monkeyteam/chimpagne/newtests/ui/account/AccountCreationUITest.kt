package com.monkeyteam.chimpagne.newtests.ui.account

import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.monkeyteam.chimpagne.model.database.Database
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import com.monkeyteam.chimpagne.ui.theme.AccountCreation
import com.monkeyteam.chimpagne.viewmodels.AccountViewModel
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AccountCreationUITest {

  val database = Database()

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun testLanguageChangeWorks() {

    composeTestRule.setContent {
      val navObject = NavigationActions(rememberNavController())
      AccountCreation(navObject, AccountViewModel(database = database))
    }

    composeTestRule.onNodeWithTag("accountCreationLabel").assertExists()
    composeTestRule.onNodeWithTag("firstNameTextField").assertExists()
    composeTestRule.onNodeWithTag("lastNameTextField").assertExists()
  }
}
