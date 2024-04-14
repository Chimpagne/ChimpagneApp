package com.monkeyteam.chimpagne.viewmodels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.monkeyteam.chimpagne.model.database.ChimpagneAccount
import com.monkeyteam.chimpagne.model.database.ChimpagneAccountManager
import com.monkeyteam.chimpagne.model.database.Database
import com.monkeyteam.chimpagne.model.location.Location
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AccountViewModel(
    private val accountManager: ChimpagneAccountManager = Database.instance.accountManager
) : ViewModel() {

  private val _loggedIn = MutableStateFlow<Boolean?>(null)
  val loggedToAChimpagneAccount: StateFlow<Boolean?> = _loggedIn

  private val _userAccount = MutableStateFlow(ChimpagneAccount())
  val userChimpagneAccount: StateFlow<ChimpagneAccount> = _userAccount

  private val _tempAccount = MutableStateFlow(ChimpagneAccount())
  val tempChimpagneAccount: StateFlow<ChimpagneAccount> = _tempAccount

  private val _imageUri = MutableStateFlow<Uri?>(null)
  val imageUri: StateFlow<Uri?> = _imageUri

  private val _tempImageUri = MutableStateFlow<Uri?>(null)
  val tempImageUri: StateFlow<Uri?> = _tempImageUri

  fun loginToChimpagneAccount(
      email: String,
      onSuccess: (ChimpagneAccount?) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    viewModelScope.launch {
      accountManager.getAccountByEmail(
          email,
          onSuccess = {
            Log.d("AccountViewModel", "Fetched user account: $it")
            if (it != null) _userAccount.value = it
            else _userAccount.value = ChimpagneAccount(email = email)
            _loggedIn.value = it != null
            getPicture()
            onSuccess(it)
          },
          onFailure = {
            Log.e("AccountViewModel", "Failed to fetch user account", it)
            onFailure(it)
          })
    }
  }

  fun logoutFromChimpagneAccount() {
    _userAccount.value = ChimpagneAccount()
  }

  fun putUpdatedAccount(onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit = {}) {
    var tempAccount = _tempAccount.value
    if (tempAccount.email == "") {
      Log.e("AccountViewModel", "Account email is invalid, can't be added to database")
      onFailure(Exception("Invalid account email"))
      return
    }

    viewModelScope.launch {
      if (_tempImageUri.value == null) {
        Log.e("AccountViewModel", "No image to upload")
        uploadAccount(tempAccount, onSuccess, onFailure)
      } else {
        Log.d(
            "AccountViewModel",
            "Image url account before: ${_tempAccount.value.profilePictureLink}")
        accountManager.uploadImage(
            _tempImageUri.value!!,
            onSuccess = { downloadUrl ->
              tempAccount = tempAccount.copy(profilePictureLink = downloadUrl)
              _imageUri.value = _tempImageUri.value
              uploadAccount(tempAccount, onSuccess, onFailure)
            },
            onFailure = {
              Log.e("AccountViewModel", "Failed to upload image", it)
              uploadAccount(tempAccount, onSuccess, onFailure)
            })
      }
    }
  }

  private fun uploadAccount(
      tempAccount: ChimpagneAccount,
      onSuccess: () -> Unit = {},
      onFailure: (Exception) -> Unit = {}
  ) {
    accountManager.updateAccount(
        account = tempAccount,
        onSuccess = {
          Log.d("AccountViewModel", "Account updated")
          Log.d("AccountViewModel", "Imague url account after: ${tempAccount.profilePictureLink}")
          _userAccount.value = tempAccount
          onSuccess()
        },
        onFailure = { exception ->
          Log.e("AccountViewModel", "Failed to update account", exception)
          onFailure(exception)
        })
  }

  fun copyRealToTemp() {
    _tempAccount.value = _userAccount.value
    _tempImageUri.value = _imageUri.value
  }

  fun createAccount(onSuccess: () -> Unit = {}, onFailure: () -> Unit = {}) {
    _tempAccount.value = _tempAccount.value.copy(email = _userAccount.value.email)
    putUpdatedAccount(
        {
          _loggedIn.value = true
          Log.d("AccountViewModel", "Account created")
          onSuccess()
        },
        {
          Log.e("AccountViewModel", "Failed to create account")
          onFailure()
        })
  }

  fun updatePicture(uri: Uri) {
    _tempImageUri.value = uri
    Log.d("AccountViewModel", "Updated profile picture URI to $uri")
  }

  fun getPicture() {
    if (userChimpagneAccount.value.profilePictureLink != "") {
      accountManager.downloadImage(
          userChimpagneAccount.value.profilePictureLink,
          onSuccess = {
            Log.d("AccountViewModel", "Got image from link: $it")
            _imageUri.value = it
          },
          onFailure = { Log.e("AccountViewModel", "Failed to get image from link", it) })
    }
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
}

data class AccountState(
    val userAccount: ChimpagneAccount = ChimpagneAccount(),
    val tempAccount: ChimpagneAccount = ChimpagneAccount(),
    val loggedIn: Boolean? = null,
    val imageUri: Uri? = null,
    val tempImageUri: Uri? = null
)
