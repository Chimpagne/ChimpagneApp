package com.monkeyteam.chimpagne.end2end

import android.Manifest
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.google.firebase.auth.FirebaseAuth
import com.monkeyteam.chimpagne.model.database.ChimpagneAccount
import com.monkeyteam.chimpagne.model.database.Database
import com.monkeyteam.chimpagne.newtests.initializeTestDatabase
import com.monkeyteam.chimpagne.ui.navigation.NavigationGraph
import com.monkeyteam.chimpagne.ui.navigation.Route
import com.monkeyteam.chimpagne.viewmodels.AccountViewModel
import com.monkeyteam.chimpagne.viewmodels.AccountViewModelFactory
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AccountCreation {
  val timeout: Long = 3000 // in milliseconds

  val database = Database()
  val account =
      ChimpagneAccount(firebaseAuthUID = "darth", firstName = "Jar Jar", lastName = "Binks")

  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule
  val mRuntimePermissionRule: GrantPermissionRule =
      GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION)

  @Before
  fun init() {
    mockkStatic(FirebaseAuth::class)
    every { FirebaseAuth.getInstance().currentUser } returns mockk(relaxed = true)
    every { FirebaseAuth.getInstance().currentUser?.uid } returns account.firebaseAuthUID

    initializeTestDatabase()
  }

  @Test
  fun accountCreationEnd2End() {
    lateinit var navController: NavHostController
    lateinit var accountViewModel: AccountViewModel

    composeTestRule.setContent {
      navController = rememberNavController()
      accountViewModel = viewModel(factory = AccountViewModelFactory(database))

      NavigationGraph(
          navController = navController, accountViewModel = accountViewModel, database = database)
    }

    composeTestRule.waitUntil(timeout) {
      navController.currentDestination?.route == Route.ACCOUNT_CREATION_SCREEN
    }
    composeTestRule.onNodeWithTag("first_name_field").performTextInput(account.firstName)
    composeTestRule.onNodeWithTag("last_name_field").performTextInput(account.lastName)
    composeTestRule.onNodeWithTag("submit_button").performClick()

    composeTestRule.waitUntil(timeout) {
      navController.currentDestination?.route == Route.HOME_SCREEN
    }
    composeTestRule.onNodeWithTag("account_settings_button").performClick()

    composeTestRule.waitUntil(timeout) {
      navController.currentDestination?.route == Route.ACCOUNT_SETTINGS_SCREEN
    }
    composeTestRule.onNodeWithText(account.firstName, useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithText(account.lastName, useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag("edit_account_button").performClick()

    composeTestRule.waitUntil(timeout) {
      navController.currentDestination?.route == Route.ACCOUNT_EDIT_SCREEN
    }
    composeTestRule.onNodeWithTag("first_name_field").performTextClearance()
    composeTestRule.onNodeWithTag("first_name_field").performTextInput("Sith")
    composeTestRule.onNodeWithTag("last_name_field").performTextClearance()
    composeTestRule.onNodeWithTag("last_name_field").performTextInput("Lord")
    composeTestRule.onNodeWithTag("submit_button").performClick()

    composeTestRule.waitUntil(timeout) {
      navController.currentDestination?.route == Route.ACCOUNT_SETTINGS_SCREEN
    }
    composeTestRule.onNodeWithText("Sith", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithText("Lord", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag("account_settings_logout_button").performClick()

    composeTestRule.waitUntil(timeout) {
      navController.currentDestination?.route == Route.LOGIN_SCREEN
    }
  }
}
