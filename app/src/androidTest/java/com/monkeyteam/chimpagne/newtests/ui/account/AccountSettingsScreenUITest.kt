package com.monkeyteam.chimpagne.newtests.ui.account

import AccountSettingsScreen
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.monkeyteam.chimpagne.model.database.Database
import com.monkeyteam.chimpagne.viewmodels.AccountViewModel
import junit.framework.TestCase.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AccountSettingsScreenUITest {

  val database = Database()

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun testSettingsCorrect() {

    var goBackPressed = false
    var editRequested = false
    var logoutPressed = false
    composeTestRule.setContent {
      AccountSettingsScreen(
          AccountViewModel(database = database),
          { goBackPressed = true },
          { editRequested = true },
          { logoutPressed = true })
    }

    composeTestRule.onNodeWithTag("account_settings_first_name").assertIsDisplayed()
    composeTestRule.onNodeWithTag("account_settings_last_name").assertIsDisplayed()

    composeTestRule.onNodeWithTag("go_back_button").performClick()
    assertTrue(goBackPressed)
    composeTestRule.onNodeWithTag("edit_account_button").performClick()
    assertTrue(editRequested)
    composeTestRule.onNodeWithTag("account_settings_logout_button").performClick()
    assertTrue(logoutPressed)
  }
}
