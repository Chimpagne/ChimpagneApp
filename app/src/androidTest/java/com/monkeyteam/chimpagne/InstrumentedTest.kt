package com.monkeyteam.chimpagne

import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.monkeyteam.chimpagne.model.location.Location
import com.monkeyteam.chimpagne.ui.AccountEdit
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import com.monkeyteam.chimpagne.ui.theme.AccountCreation
import com.monkeyteam.chimpagne.ui.utilities.MapContainer
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

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

class TestMap {
  @Test
  fun check_adding_markers_works() = runBlocking {
    val map = MapContainer()
    val locParis = Location("Paris", 48.8566, 2.3522)
    val locBerlin = Location("Berlin", 52.5200, 13.4050)
    val locMadrid = Location("Madrid", 40.4168, 3.7038)

    map.addMarker(locParis)
    map.addMarker(locBerlin)
    map.addMarker(locMadrid)

    assertEquals(listOf(locParis, locBerlin, locMadrid), map.markers.value)
  }
}
