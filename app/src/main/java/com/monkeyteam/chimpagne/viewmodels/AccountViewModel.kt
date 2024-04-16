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

  private val _uiState = MutableStateFlow(AccountUIState())
  val uiState: StateFlow<AccountUIState> = _uiState

  fun loginToChimpagneAccount(
      email: String,
      onSuccess: (ChimpagneAccount?) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    _uiState.value = _uiState.value.copy(currentUserEmail = email)
    viewModelScope.launch {
      accountManager.getAccountByEmail(
          email,
          onSuccess = {
            Log.d("AccountViewModel", "Fetched user account: $it")
            _uiState.value = _uiState.value.copy(currentUserAccount = it)
            if (it != null) {
              accountManager.signInTo(it)
            }
            if (it != null && it.profilePictureLink != "") {
              getPicture(onSuccess = { onSuccess(it) }, onFailure = onFailure)
            } else {
              onSuccess(it)
            }
          },
          onFailure = {
            Log.e("AccountViewModel", "Failed to fetch user account", it)
            onFailure(it)
          })
    }
  }

  fun logoutFromChimpagneAccount() {
    _uiState.value = AccountUIState()
    accountManager.signOut()
  }

  fun submitUpdatedAccount(onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit = {}) {
    if (_uiState.value.currentUserEmail == null) {
      Log.e("AccountViewModel", "Account email is invalid, can't be added to database")
      onFailure(Exception("Invalid account email"))
      return
    }

    viewModelScope.launch {
      accountManager.uploadImage(
          _uiState.value.tempImageUri!!,
          onSuccess = { downloadUrl ->
            _uiState.value.tempAccount =
                _uiState.value.tempAccount.copy(profilePictureLink = downloadUrl)
            _uiState.value.imageUri = _uiState.value.tempImageUri
            uploadAccount(_uiState.value.tempAccount, onSuccess, onFailure)
          },
          onFailure = {
            Log.e("AccountViewModel", "Failed to upload image", it)
            uploadAccount(_uiState.value.tempAccount, onSuccess, onFailure)
          })
    }
  }

  private fun uploadAccount(
      tempAccount: ChimpagneAccount,
      onSuccess: () -> Unit = {},
      onFailure: (Exception) -> Unit = {}
  ) {
    accountManager.updateCurrentAccount(
        account = tempAccount,
        onSuccess = {
          Log.d("AccountViewModel", "Account updated")
          _uiState.value = _uiState.value.copy(currentUserAccount = tempAccount)
          onSuccess()
        },
        onFailure = { exception ->
          Log.e("AccountViewModel", "Failed to update account", exception)
          onFailure(exception)
        })
  }

  fun copyRealToTemp() {
    _uiState.value.tempAccount = _uiState.value.currentUserAccount!!
    _uiState.value.tempImageUri = _uiState.value.imageUri
  }

  fun updateTempAccount(newTempAccount: ChimpagneAccount) {
    _uiState.value = _uiState.value.copy(tempAccount = newTempAccount)
  }

  fun updatePicture(uri: Uri) {
    _uiState.value = _uiState.value.copy(tempImageUri = uri)
  }

  fun getPicture(onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit = {}) {
    val currentPictureURI = _uiState.value.currentUserAccount!!.profilePictureLink
    if (currentPictureURI != "") {
      accountManager.downloadImage(
          currentPictureURI,
          onSuccess = {
            Log.d("AccountViewModel", "Got image from link: $it")
            _uiState.value.imageUri = it
            onSuccess()
          },
          onFailure = { Log.e("AccountViewModel", "Failed to get image from link", it) })
    }
  }

  fun updateFirstName(firstName: String) {
    updateTempAccount(_uiState.value.tempAccount.copy(firstName = firstName))
    Log.e("AccountViewModel", "Updated first name to $firstName")
  }

  fun updateLastName(lastName: String) {
    updateTempAccount(_uiState.value.tempAccount.copy(lastName = lastName))
    Log.e("AccountViewModel", "Updated last name to $lastName")
  }

  fun updateLocation(location: Location) {
    updateTempAccount(_uiState.value.tempAccount.copy(location = location))
    Log.d("AccountViewModel", "Updated location name to $location")
  }
}

/**
 * [currentUserAccount] this field will be null if the user isn't sign in to Firebase or if he
 * doesn't have Chimpagne Account [currentUserEmail] this field will be null iff he isn't sign in to
 * Firebase [tempAccount] this field is used to store temporal data in forms that will be submitted
 */
data class AccountUIState(
    var currentUserAccount: ChimpagneAccount? = null,
    var currentUserEmail: String? = null,
    var tempAccount: ChimpagneAccount = ChimpagneAccount(),
    var imageUri: Uri? = null,
    var tempImageUri: Uri? = null
)
