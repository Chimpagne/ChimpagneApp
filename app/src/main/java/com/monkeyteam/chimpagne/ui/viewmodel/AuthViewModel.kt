package com.monkeyteam.chimpagne.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthViewModel : ViewModel() {
  private val _isAuthenticated =
      MutableStateFlow<Boolean?>(
          null) // StateFlow can't be null, so initialize with null or a Boolean value
  val isAuthenticated: StateFlow<Boolean?> = _isAuthenticated.asStateFlow()

  private val authStateListener =
      FirebaseAuth.AuthStateListener { firebaseAuth ->
        _isAuthenticated.value = firebaseAuth.currentUser != null
      }

  init {
    FirebaseAuth.getInstance().addAuthStateListener(authStateListener)
  }

  override fun onCleared() {
    super.onCleared()
    FirebaseAuth.getInstance().removeAuthStateListener(authStateListener)
  }
}
