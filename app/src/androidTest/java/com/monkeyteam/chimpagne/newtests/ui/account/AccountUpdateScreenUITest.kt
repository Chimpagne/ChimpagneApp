package com.monkeyteam.chimpagne.newtests.ui.account

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.monkeyteam.chimpagne.model.database.Database
import com.monkeyteam.chimpagne.newtests.SLEEP_AMOUNT_MILLIS
import com.monkeyteam.chimpagne.newtests.TEST_ACCOUNTS
import com.monkeyteam.chimpagne.newtests.initializeTestDatabase
import com.monkeyteam.chimpagne.ui.account.AccountUpdateScreen
import com.monkeyteam.chimpagne.viewmodels.AccountViewModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AccountUpdateScreenUITest {

  val database = Database()

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun init() {
    initializeTestDatabase()
  }

  @Test
  fun testAccountUpdateScreen() {

    val accountViewModel = AccountViewModel(database = database)

    accountViewModel.loginToChimpagneAccount(TEST_ACCOUNTS[0].firebaseAuthUID, {}, {})
    while (accountViewModel.uiState.value.loading) {}

    var goBackPressed = false
    var accountModified = false
    composeTestRule.setContent {
      AccountUpdateScreen(
          accountViewModel, { goBackPressed = true }, { accountModified = true }, editMode = true)
    }

    composeTestRule.onNodeWithTag("profile_icon").assertExists()
    composeTestRule.onNodeWithTag("first_name_label", useUnmergedTree = true).assertExists()
    composeTestRule.onNodeWithTag("last_name_label", useUnmergedTree = true).assertExists()

    composeTestRule.onNodeWithTag("first_name_field").performTextClearance()
    composeTestRule.onNodeWithTag("last_name_field").performTextClearance()
    composeTestRule.onNodeWithTag("submit_button").performClick()
    assertFalse(accountModified)

    assertEquals("", accountViewModel.uiState.value.tempAccount.firstName)
    assertEquals("", accountViewModel.uiState.value.tempAccount.lastName)
    composeTestRule.onNodeWithTag("first_name_field").performTextInput("Monkey")
    composeTestRule.onNodeWithTag("last_name_field").performTextInput("Prince")
    assertEquals("Monkey", accountViewModel.uiState.value.tempAccount.firstName)
    assertEquals("Prince", accountViewModel.uiState.value.tempAccount.lastName)

    composeTestRule.onNodeWithTag("go_back_button").performClick()
    assertTrue(goBackPressed)
    composeTestRule.onNodeWithTag("submit_button").performClick()
    while (accountViewModel.uiState.value.loading) {}
    Thread.sleep(SLEEP_AMOUNT_MILLIS)
    assertTrue(accountModified)
  }
}
