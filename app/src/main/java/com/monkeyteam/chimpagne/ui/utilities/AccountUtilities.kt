package com.monkeyteam.chimpagne.ui.utilities

import android.content.Context
import android.widget.Toast
import androidx.core.content.ContextCompat.getString
import com.monkeyteam.chimpagne.R
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import com.monkeyteam.chimpagne.ui.navigation.Route

// This function checks if the user is logged in or not and shows a Toast and redirects to the
// Login screen if the user is a guest
fun promptLogin(context: Context, navActions: NavigationActions) {
  Toast.makeText(context, getString(context, R.string.login_to_continue), Toast.LENGTH_LONG).show()
  navActions.navigateTo(Route.LOGIN_SCREEN)
}
