package com.monkeyteam.chimpagne.ui.utilities

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.google.firebase.auth.FirebaseAuth
import com.monkeyteam.chimpagne.R

@Composable
fun GoogleAuthentication(
    onSuccessfulLogin: (uid: String) -> Unit,
    onLoginFailed: () -> Unit,
    modifier: Modifier = Modifier,
) {
  val signInLauncher =
      rememberLauncherForActivityResult(
          FirebaseAuthUIActivityResultContract(),
      ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
          // Successfully signed in
          onSuccessfulLogin(FirebaseAuth.getInstance().currentUser?.uid!!)
        } else {
          // Sign in failed. If response is null the user canceled the
          // sign-in flow using the back button. Otherwise check
          // response.getError().getErrorCode() and handle the error.
          // ...

          // error code: result.idpResponse?.error?.errorCode
          onLoginFailed()
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
      modifier = modifier.testTag("googleAuthenticationButton")) {
        Row {
          Image(
              painter = painterResource(id = R.drawable.google_logo),
              contentDescription = "Google Logo",
              modifier = Modifier.size(40.dp).align(Alignment.CenterVertically))
          Text(
              text = stringResource(id = R.string.sign_in_with_google),
              modifier = Modifier.align(Alignment.CenterVertically).padding(start = 25.dp))
        }
      }
}
