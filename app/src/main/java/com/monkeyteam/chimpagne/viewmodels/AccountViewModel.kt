package com.monkeyteam.chimpagne.viewmodels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.monkeyteam.chimpagne.model.database.ChimpagneAccount
import com.monkeyteam.chimpagne.model.database.ChimpagneAccountUID
import com.monkeyteam.chimpagne.model.database.Database
import com.monkeyteam.chimpagne.model.location.Location
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AccountViewModel(database: Database) : ViewModel() {

  private val accountManager = database.accountManager

  private val _uiState = MutableStateFlow(AccountUIState())
  val uiState: StateFlow<AccountUIState> = _uiState

  fun loginToChimpagneAccount(
      uid: String,
      onSuccess: (ChimpagneAccount?) -> Unit,
      onFailure: (Exception) -> Unit,
  ) {
    _uiState.value = _uiState.value.copy(currentUserUID = uid, loading = true)
    viewModelScope.launch {
      accountManager.getAccountWithProfilePicture(
          uid,
          onSuccess = { account, profilePicture ->
            Log.d("AccountViewModel", "Fetched user account: $account with URI: $profilePicture")
            _uiState.value =
                _uiState.value.copy(
                    currentUserAccount = account,
                    currentUserProfilePicture = profilePicture,
                    loading = false)
            if (account != null) {
              accountManager.signInTo(account)
            }
            onSuccess(account)
          },
          onFailure = {
            Log.e("AccountViewModel", "Failed to fetch user account", it)
            _uiState.value = _uiState.value.copy(loading = false)
            onFailure(it)
          })
    }
  }

  fun getProfilePictureUri(uid: String, onSuccess: (Uri?) -> Unit) {
    accountManager.fetchProfilePictureUri(uid, onSuccess)
  }

  fun logoutFromChimpagneAccount() {
    _uiState.value = AccountUIState()
    accountManager.signOut()
  }

  fun submitUpdatedAccount(onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit = {}) {
    if (_uiState.value.currentUserUID == null) {
      Log.e("AccountViewModel", "Account UID is invalid, can't be added to database")
      onFailure(Exception("Invalid account UID"))
      return
    }

    val newAccount =
        _uiState.value.tempAccount.copy(firebaseAuthUID = _uiState.value.currentUserUID!!)
    val newProfilePictureUri =
        if (_uiState.value.tempProfilePicture != _uiState.value.currentUserProfilePicture)
            _uiState.value.tempProfilePicture
        else _uiState.value.currentUserProfilePicture

    _uiState.value = _uiState.value.copy(loading = true)
    viewModelScope.launch {
      accountManager.updateCurrentAccount(
          newAccount,
          newProfilePictureUri,
          {
            _uiState.value =
                _uiState.value.copy(
                    currentUserAccount = newAccount,
                    tempAccount = ChimpagneAccount(),
                    currentUserProfilePicture = newProfilePictureUri,
                    tempProfilePicture = null,
                    loading = false)
            onSuccess()
          },
          {
            Log.e("AccountViewModel", "Failed to update account", it)
            _uiState.value = _uiState.value.copy(loading = false)
            onFailure(it)
          })
    }
  }

  fun copyRealToTemp() {
    _uiState.value =
        _uiState.value.copy(
            tempAccount = _uiState.value.currentUserAccount ?: ChimpagneAccount(),
            tempProfilePicture = _uiState.value.currentUserProfilePicture)
  }

  private fun updateTempAccount(newTempAccount: ChimpagneAccount) {
    _uiState.value = _uiState.value.copy(tempAccount = newTempAccount)
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

  fun updateProfilePicture(uri: Uri) {
    _uiState.value = _uiState.value.copy(tempProfilePicture = uri)
    Log.d("AccountViewModel", "Updated Profile Picture to $uri")
  }

  fun isUserLoggedIn(): Boolean {
    return FirebaseAuth.getInstance().currentUser != null
  }

  fun fetchAccounts(accountUIDs: List<ChimpagneAccountUID>) {
    _uiState.value = _uiState.value.copy(loading = true)
    accountManager.getAccounts(
        accountUIDs,
        { _uiState.value = _uiState.value.copy(fetchedAccounts = it, loading = false) },
        { _uiState.value = _uiState.value.copy(loading = false) })
  }
}

/**
 * [currentUserUID] this field will be null iff he isn't sign in to Firebase [currentUserAccount]
 * this field will be null if the user isn't sign in to Firebase or if he doesn't have Chimpagne
 * Account [tempAccount] this field is used to store temporary data in forms that will be submitted
 */
data class AccountUIState(
    val currentUserUID: String? = null,
    val currentUserAccount: ChimpagneAccount? = null,
    val tempAccount: ChimpagneAccount = ChimpagneAccount(),
    val currentUserProfilePicture: Uri? = null,
    val tempProfilePicture: Uri? = null,
    val fetchedAccounts: Map<ChimpagneAccountUID, ChimpagneAccount?> = hashMapOf(),
    val loading: Boolean = false
)

class AccountViewModelFactory(private val database: Database) : ViewModelProvider.Factory {
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    return AccountViewModel(database) as T
  }
}
