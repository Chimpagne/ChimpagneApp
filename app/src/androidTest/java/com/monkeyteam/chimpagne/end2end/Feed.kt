package com.monkeyteam.chimpagne.end2end

import android.Manifest
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
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
class FeedEnd2EndTest {
  val timeout: Long = 5000 // in milliseconds

  val database = Database()
  val account =
      ChimpagneAccount(
          firebaseAuthUID = "ovoland",
          firstName = "Graphics",
          lastName = "Expert",
          joinedEvents = hashMapOf())

  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule
  val mRuntimePermissionRule: GrantPermissionRule =
      GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION)

  @Before
  fun init() {
    mockkStatic(FirebaseAuth::class)
    every { FirebaseAuth.getInstance().currentUser } returns mockk(relaxed = true)
    every { FirebaseAuth.getInstance().currentUser?.uid } returns account.firebaseAuthUID

    initializeTestDatabase(accounts = listOf(account))
  }

  @Test
  fun feedEnd2EndTest() {
    lateinit var navController: NavHostController
    lateinit var accountViewModel: AccountViewModel

    composeTestRule.setContent {
      navController = rememberNavController()
      accountViewModel = viewModel(factory = AccountViewModelFactory(database))

      NavigationGraph(
          navController = navController, accountViewModel = accountViewModel, database = database)
    }

    composeTestRule.waitUntil(timeout) {
      composeTestRule.onNodeWithTag("request_location_permission_button").isDisplayed()
    }

    composeTestRule.onNodeWithTag("request_location_permission_button").performClick()
    Thread.sleep(20000)

    composeTestRule.onNodeWithTag("account_settings_button").performClick()
    composeTestRule.waitUntil(timeout) {
      composeTestRule.onNodeWithTag("go_back_button").isDisplayed()
    }
    composeTestRule.onNodeWithTag("go_back_button").performClick()
    Thread.sleep(20000)
    composeTestRule.waitUntil(timeout) {
      composeTestRule.onNodeWithTag("screen title").isDisplayed()
    }
    Thread.sleep(20000)
  }
}
