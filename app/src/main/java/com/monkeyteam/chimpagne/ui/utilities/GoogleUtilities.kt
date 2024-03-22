package com.monkeyteam.chimpagne.ui.utilities

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.monkeyteam.chimpagne.R


@Composable
fun GoogleAuthentication(successfulLogin: () -> Unit){
    val signInLauncher =
        rememberLauncherForActivityResult(
            FirebaseAuthUIActivityResultContract(),
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                successfulLogin()
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    val providers =
        arrayListOf(
            AuthUI.IdpConfig.GoogleBuilder().build(),
        )

    // Create and launch sign-in intent
    val signInIntent =
        AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).build()

    OutlinedButton(
        onClick = { signInLauncher.launch(signInIntent) },
        modifier = Modifier.size(250.dp, 40.dp).testTag("LoginButton")) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start) {
            Image(
                painter = painterResource(id = R.drawable.google_logo),
                contentDescription = "Google Logo")
            Spacer(modifier = Modifier.width(20.dp))
            Text(text = "Sign in with Google", color = Color.DarkGray)
        }
    }
}

fun getFireBaseUser(): FirebaseUser?{
    return FirebaseAuth.getInstance().currentUser
}



