package com.monkeyteam.chimpagne

import androidx.compose.material3.Text
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.printToLog
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.monkeyteam.chimpagne.ui.HomeScreen
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import com.monkeyteam.chimpagne.ui.theme.AccountCreation
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.w3c.dom.Text

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
  @Test
  fun useAppContext() {
    // Context of the app under test.
    val appContext = InstrumentationRegistry.getInstrumentation().targetContext
    assertEquals("com.monkeyteam.chimpagne", appContext.packageName)
  }
}

@RunWith(AndroidJUnit4::class)
class AccountCreationUITest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun testLanguageChangeWorks() {
    composeTestRule.setContent { AccountCreation() }

    composeTestRule.onNodeWithTag("accountCreationLabel").assertTextContains("Créer votre compte")
    composeTestRule.onNodeWithTag("firstNameTextField").assertTextContains("Prénom")
    composeTestRule.onNodeWithTag("lastNameTextField").assertTextContains("Nom de famille")
    composeTestRule.onNodeWithTag("createAccountButton").assertTextContains("Créer un compte")

    composeTestRule.onNodeWithTag("changeLanguageSwitch").performClick()

    composeTestRule.onNodeWithTag("accountCreationLabel").assertTextContains("Create your Account")
    composeTestRule.onNodeWithTag("firstNameTextField").assertTextContains("First Name")
    composeTestRule.onNodeWithTag("lastNameTextField").assertTextContains("Last Name")
    composeTestRule.onNodeWithTag("createAccountButton").assertTextContains("Create Account")
  }

  @Test
  fun textInputWorks() {
    composeTestRule.setContent { AccountCreation() }

    composeTestRule.onNodeWithTag("firstNameTextField").performTextInput("John")
    composeTestRule.onNodeWithTag("lastNameTextField").performTextInput("Doe")
    composeTestRule.onNodeWithTag("locationTextField").performTextInput("Paris")

    composeTestRule.onNodeWithTag("firstNameTextField").assertTextContains("John")
    composeTestRule.onNodeWithTag("lastNameTextField").assertTextContains("Doe")
    composeTestRule.onNodeWithTag("locationTextField").assertTextContains("Paris")
  }
}

@RunWith(AndroidJUnit4::class)
class HomeScreenUITest {
  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun testHomeScreen() {
    val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
    composeTestRule.setContent {
        val navActions = NavigationActions(navController)
        HomeScreen(navActions)
      }

    composeTestRule.onNodeWithTag("MyEventsButton").assertTextContains("MY EVENTS")
    composeTestRule.onNodeWithTag("JoinEventButton").assertTextContains("JOIN AN EVENT")
    composeTestRule.onNodeWithTag("OrganizeEventButton").assertTextContains("ORGANIZE AN EVENT")

    composeTestRule.onNodeWithTag("ProfileIcon").assertIsDisplayed()

    composeTestRule.onNodeWithTag("MyEventsButton").isDisplayed()
    composeTestRule.onNodeWithTag("JoinEventButton").isDisplayed()
    composeTestRule.onNodeWithTag("OrganizeEventButton").isDisplayed()

  }
}
