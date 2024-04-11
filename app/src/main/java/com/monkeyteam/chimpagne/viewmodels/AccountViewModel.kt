package com.monkeyteam.chimpagne.viewmodels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.monkeyteam.chimpagne.model.database.ChimpagneAccount
import com.monkeyteam.chimpagne.model.database.Database
import com.monkeyteam.chimpagne.model.location.Location
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AccountViewModel : ViewModel() {

  private val accountManager = Database.instance.accountManager

  private val _accountExists = MutableStateFlow<Boolean?>(null)
  val accountExists: StateFlow<Boolean?> = _accountExists

  private val _account = MutableStateFlow(ChimpagneAccount())
  val account: StateFlow<ChimpagneAccount> = _account

  private val _tempAccount = MutableStateFlow(ChimpagneAccount())
  val tempAccount: StateFlow<ChimpagneAccount> = _tempAccount

  init {
    //    Log.d("AccountViewModel", "AccountViewModel initialized")
  }

  fun fetchAccount(
      email: String,
      onSuccess: (ChimpagneAccount?) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    viewModelScope.launch {
      accountManager.getAccountByEmail(
          email,
          onSuccess = {
            Log.e("AccountViewModel", "Fetched user account: $account")
            if (it != null) _account.value = it
            else _account.value = ChimpagneAccount(email = email)
            _accountExists.value = it != null
            onSuccess(it)
          },
          onFailure = {
            Log.e("AccountViewModel", "Failed to fetch user account", it)
            onFailure(it)
          })
    }
  }

  fun putUpdatedAccount(onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit = {}) {
    val account = _tempAccount.value
    if (account.email == "") {
      Log.e("AccountViewModel", "Account email is invalid, can't be added to database")
      onFailure(Exception("Invalid account email"))
      return
    }

    viewModelScope.launch {
      accountManager.updateAccount(
          account = account,
          onSuccess = {
            Log.d("AccountViewModel", "Account updated")
            _account.value = account
            onSuccess()
          },
          onFailure = { exception ->
            Log.e("AccountViewModel", "Failed to update account", exception)
            onFailure(exception)
          })
    }
  }

  fun moveUserAccountToTemp() {
    _tempAccount.value = _account.value
  }

  fun createAccount() {
    _tempAccount.value = _tempAccount.value.copy(email = _account.value.email)
    putUpdatedAccount(
        {
          _accountExists.value = true
          Log.d("AccountViewModel", "Account created")
        },
        { Log.e("AccountViewModel", "Failed to create account") })
  }

  fun updateEmail(email: String) {
    _account.value = _account.value.copy(email = email)
    Log.d("AccountViewModel", "Updated profile email to $email")
  }

  fun updateUri(uri: Uri) {
    _tempAccount.value = _tempAccount.value.copy(profilePictureUri = uri)
    Log.d("AccountViewModel", "Updated profile picture URI to $uri")
  }

  fun updateFirstName(firstName: String) {
    _tempAccount.value = _tempAccount.value.copy(firstName = firstName)
    Log.e("AccountViewModel", "Updated first name to $firstName")
  }

  fun updateLastName(lastName: String) {
    _tempAccount.value = _tempAccount.value.copy(lastName = lastName)
    Log.e("AccountViewModel", "Updated last name to $lastName")
  }

  fun updateLocation(location: Location) {
    _tempAccount.value = _tempAccount.value.copy(location = location)
    Log.d("AccountViewModel", "Updated location name to $location")
  }

  fun updatePreferredLanguageEnglish(preferredLanguageEnglish: Boolean) {
    _tempAccount.value =
        _tempAccount.value.copy(preferredLanguageEnglish = preferredLanguageEnglish)
    if (preferredLanguageEnglish) {
      Log.d("AccountViewModel", "Updated preferred language to English")
    } else {
      Log.d("AccountViewModel", "Updated preferred language to French")
    }
  }
}
