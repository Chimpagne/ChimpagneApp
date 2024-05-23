package com.monkeyteam.chimpagne.ui.utilities

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.monkeyteam.chimpagne.R
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import com.monkeyteam.chimpagne.ui.navigation.Route

// This function checks if the user is logged in or not and shows a Toast and redirects to the
// Login screen if the user is a guest
@Composable
fun PromptLogin(context: Context, navActions: NavigationActions) {
  Toast.makeText(context, stringResource(id = R.string.login_to_continue), Toast.LENGTH_LONG).show()
  navActions.navigateTo(Route.LOGIN_SCREEN)
}
