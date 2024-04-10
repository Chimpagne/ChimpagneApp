package com.monkeyteam.chimpagne.ui.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.monkeyteam.chimpagne.model.database.ChimpagneAccount
import com.monkeyteam.chimpagne.model.database.Database
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AccountViewModel(emailInit: String) : ViewModel() {

  private val accountManager = Database.instance.accountManager

  private val _userEmail = MutableStateFlow(emailInit)
  private val userEmail: StateFlow<String> = _userEmail.asStateFlow()

  private val _userAccount = MutableStateFlow<ChimpagneAccount?>(null)
  val userAccount: StateFlow<ChimpagneAccount?> = _userAccount.asStateFlow()

  init {
    fetchUserAccount()
    Log.d("AccountViewModel", "AccountViewModel initialized")
  }

  fun fetchUserAccount() {
    val email = userEmail.value // or another source if your logic requires
    viewModelScope.launch {
      accountManager.GetSpecificAccount(
          userEmail = email,
          onSuccess = { account ->
            _userAccount.value = account
            Log.e("AccountViewModel", "Fetched user account: $account")
          },
          onFailure = { exception ->
            Log.e("AccountViewModel", "Failed to fetch user account", exception)
          })
    }
  }

  fun putUpdatedAccount() {
    val account = userAccount.value
    if (account == null) {
      Log.e("AccountViewModel", "Account is null, can't be added to database")
      return
    }

    viewModelScope.launch {
      accountManager.PutSpecificAccount(
          account = account,
          onSuccess = { Log.d("AccountViewModel", "Account updated") },
          onFailure = { exception ->
            Log.e("AccountViewModel", "Failed to update account", exception)
          })
    }
  }

  fun updateUri(uri: Uri) {
    val newAccount = userAccount.value?.copy(profilePictureUri = uri)
    _userAccount.value = newAccount
    Log.e("AccountViewModel", "Updated profile picture URI to $uri")
  }

  fun updateFirstName(firstName: String) {
    val newAccount = userAccount.value?.copy(firstName = firstName)
    _userAccount.value = newAccount
    Log.e("AccountViewModel", "Updated first name to $firstName")
  }

  fun updateLastName(lastName: String) {
    val newAccount = userAccount.value?.copy(lastName = lastName)
    _userAccount.value = newAccount
    Log.e("AccountViewModel", "Updated last name to $lastName")
  }

  fun updateLocationName(locationName: String) {
    val newAccount =
        userAccount.value?.copy(location = userAccount.value?.location?.copy(name = locationName))
    _userAccount.value = newAccount
    Log.e("AccountViewModel", "Updated location name to $locationName")
  }

  fun updatePreferredLanguageEnglish(preferredLanguageEnglish: Boolean) {
    val newAccount = userAccount.value?.copy(preferredLanguageEnglish = preferredLanguageEnglish)
    _userAccount.value = newAccount
    if (preferredLanguageEnglish) {
      Log.e("AccountViewModel", "Updated preferred language to English")
    } else {
      Log.e("AccountViewModel", "Updated preferred language to French")
    }
  }
}
