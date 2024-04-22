package com.monkeyteam.chimpagne

import DateSelector
import TimePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.monkeyteam.chimpagne.ui.LoginScreen
import com.monkeyteam.chimpagne.ui.utilities.GoogleAuthentication
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Calendar
import java.util.Locale

@RunWith(AndroidJUnit4::class)
class GoogleUtilitiesUITest {

    @get:Rule
    val composeTestRule = createComposeRule()


    @Test
    fun checkUI() {

        composeTestRule.setContent {
            GoogleAuthentication({ } , { } )
        }

        composeTestRule.onNodeWithTag("googleAuthenticationButton").assertTextContains(getSignInText())
        composeTestRule.onNodeWithContentDescription("Google Logo").assertIsDisplayed()

    }

    //Both todo with dependency injections
    @Test
    fun checkFailedLogin(){
    }

    @Test
    fun checkSuccessfulLogin(){

    }
}

@RunWith(AndroidJUnit4::class)
class ThemeUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun checkLightTheme() {
    }

}

@RunWith(AndroidJUnit4::class)
class DateSelectorTest {

    @get:Rule
    val composeTestRule = createComposeRule()

        @OptIn(ExperimentalMaterial3Api::class)
        @Test
        fun DatePickerWorks() {
            runBlocking {
                val selectedDate = Calendar.getInstance()
                var chosenDate: Calendar? = null
                composeTestRule.setContent {
                    DateSelector(selectedDate = selectedDate, onDateSelected = { chosenDate = it })
                }

                composeTestRule.onNodeWithTag("selectDate").assertIsNotDisplayed()
                composeTestRule.onNodeWithTag("selectTime").assertIsNotDisplayed()

                composeTestRule.onNodeWithTag("dateSelector").assertIsDisplayed()
                composeTestRule.onNodeWithTag("dateSelector").performClick()

                composeTestRule.onNodeWithTag("selectDate").assertIsDisplayed()
                composeTestRule.onNodeWithTag("selectDate").performClick()

                composeTestRule.onNodeWithTag("selectDate").assertIsNotDisplayed()

                assertIsWithinOne(chosenDate!!.get(Calendar.YEAR), selectedDate.get(Calendar.YEAR))
                assertIsWithinOne(chosenDate!!.get(Calendar.MONTH), selectedDate.get(Calendar.MONTH))
                assertIsWithinOne(chosenDate!!.get(Calendar.DATE), selectedDate.get(Calendar.DATE))
            }
        }

    @OptIn(ExperimentalMaterial3Api::class)
    @Test
    fun TimePickerWorks(){
        runBlocking {
            val selectedDate = Calendar.getInstance()
            var chosenDate: Calendar? = null
            composeTestRule.setContent {
                DateSelector(selectedDate = selectedDate, onDateSelected = {chosenDate = it}, selectTimeOfDay = true)
            }

            composeTestRule.onNodeWithTag("selectDate").assertIsNotDisplayed()
            composeTestRule.onNodeWithTag("selectTime").assertIsNotDisplayed()

            composeTestRule.onNodeWithTag("dateSelector").assertIsDisplayed()
            composeTestRule.onNodeWithTag("dateSelector").performClick()

            composeTestRule.onNodeWithTag("selectDate").assertIsDisplayed()
            composeTestRule.onNodeWithTag("selectDate").performClick()

            composeTestRule.onNodeWithTag("selectDate").assertIsNotDisplayed()
            composeTestRule.onNodeWithTag("selectTime").assertIsDisplayed()
            composeTestRule.onNodeWithTag("selectTime").performClick()

            composeTestRule.onNodeWithTag("selectTime").assertIsNotDisplayed()
            val currentDate = Calendar.getInstance()
            assertIsWithinOne(chosenDate!!.get(Calendar.YEAR), selectedDate.get(Calendar.YEAR))
            assertIsWithinOne(chosenDate!!.get(Calendar.MONTH), selectedDate.get(Calendar.MONTH))
            assertIsWithinOne(chosenDate!!.get(Calendar.DATE), selectedDate.get(Calendar.DATE))
            assertIsWithinOne(chosenDate!!.get(Calendar.HOUR), currentDate.get(Calendar.HOUR))
            assertIsWithinOne(chosenDate!!.get(Calendar.MINUTE), currentDate.get(Calendar.MINUTE))

        }
    }
}

@RunWith(AndroidJUnit4::class)
class LoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun checkUI() {
        composeTestRule.setContent {
            LoginScreen({})
        }

        composeTestRule.onNodeWithContentDescription("App Logo").assertIsDisplayed()
        composeTestRule.onNodeWithTag("welcome_screen_title").assertTextContains(getWelcomeScreenText())

    }

    @Test
    fun checkAlertDialog() {
        composeTestRule.setContent {
            LoginScreen({})
        }
    }
}

private fun assertIsWithinOne(toBeCompared: Int, expected: Int) {
    assert(toBeCompared == expected || toBeCompared == expected + 1 || toBeCompared == expected - 1)
}
private fun getWelcomeScreenText(): String {
    val currentLocale = Locale.getDefault()
    return if (currentLocale.language == "fr") {
        "Bienvenue à"
    } else {
        "Welcome to"
    }
}
private fun getSignInText(): String {
    val currentLocale = Locale.getDefault()
    return if (currentLocale.language == "fr") {
        "Se connecter avec Google"
    } else {
        "Sign in with Google"
    }
}
