package com.monkeyteam.chimpagne.newtests.ui.account

import AccountSettingsScreen
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
      AccountSettingsScreen(AccountViewModel(database = database), {}, {}, {})
    }

    composeTestRule.onNodeWithTag("firstNameTextField").assertTextContains("First Name")
    composeTestRule.onNodeWithTag("lastNameTextField").assertTextContains("Last Name")
  }
}
