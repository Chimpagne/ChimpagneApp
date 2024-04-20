package com.monkeyteam.chimpagne

import DateSelector
import TimePickerDialog
import androidx.activity.result.ActivityResult
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.google.firebase.auth.FirebaseAuth
import com.monkeyteam.chimpagne.model.utils.buildCalendar
import com.monkeyteam.chimpagne.ui.LoginScreen
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import com.monkeyteam.chimpagne.ui.theme.ChimpagneTheme
import com.monkeyteam.chimpagne.ui.theme.md_theme_light_background
import com.monkeyteam.chimpagne.ui.theme.md_theme_light_error
import com.monkeyteam.chimpagne.ui.theme.md_theme_light_errorContainer
import com.monkeyteam.chimpagne.ui.theme.md_theme_light_onBackground
import com.monkeyteam.chimpagne.ui.theme.md_theme_light_onError
import com.monkeyteam.chimpagne.ui.theme.md_theme_light_onErrorContainer
import com.monkeyteam.chimpagne.ui.theme.md_theme_light_onPrimary
import com.monkeyteam.chimpagne.ui.theme.md_theme_light_onPrimaryContainer
import com.monkeyteam.chimpagne.ui.theme.md_theme_light_onSecondary
import com.monkeyteam.chimpagne.ui.theme.md_theme_light_onSecondaryContainer
import com.monkeyteam.chimpagne.ui.theme.md_theme_light_onTertiary
import com.monkeyteam.chimpagne.ui.theme.md_theme_light_onTertiaryContainer
import com.monkeyteam.chimpagne.ui.theme.md_theme_light_primary
import com.monkeyteam.chimpagne.ui.theme.md_theme_light_primaryContainer
import com.monkeyteam.chimpagne.ui.theme.md_theme_light_secondary
import com.monkeyteam.chimpagne.ui.theme.md_theme_light_secondaryContainer
import com.monkeyteam.chimpagne.ui.theme.md_theme_light_tertiary
import com.monkeyteam.chimpagne.ui.theme.md_theme_light_tertiaryContainer
import com.monkeyteam.chimpagne.ui.utilities.GoogleAuthentication
import com.monkeyteam.chimpagne.viewmodels.AccountViewModel
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.verify
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
    val selectedDate = buildCalendar(1, 5, 2024, 5, 1)

    @get:Rule
    val composeTestRule = createComposeRule()

    @OptIn(ExperimentalMaterial3Api::class)
    @Test
    fun checkUI() {
        composeTestRule.setContent {
            TimePickerDialog(
                title = "Select Time",
                onDismissRequest = { /*TODO*/ }, confirmButton = { /*TODO*/ }) {

            }
            composeTestRule.onNodeWithTag("title").assertIsDisplayed()
                .assertTextContains("Select Time")

        }

        @Test
        fun checkDateSelection() {

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
        composeTestRule.onNodeWithTag("welcome_screen_title").assertIsDisplayed().assertTextContains(
            getWelcomeScreenText()
        )
        composeTestRule.onNodeWithTag("Chimpagne").assertIsDisplayed().assertTextContains("Chimpagne")
    }

    @Test
    fun checkAlertDialog() {

    }
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