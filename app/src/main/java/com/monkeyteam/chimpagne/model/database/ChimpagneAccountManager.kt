package com.monkeyteam.chimpagne.model.database

import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.StorageReference

/** Use this class to interact */
class ChimpagneAccountManager(
    private val database: Database,
    private val accounts: CollectionReference,
    private val profilePictures: StorageReference
) {

  /**
   * This field stores the current logged user's account, you can retrieve it from any class using
   *
   * @sample currentUserAccountSample
   */
  var currentUserAccount: ChimpagneAccount? = null
    private set

  private fun currentUserAccountSample() {
    // Use this to get the current logged user
    val currentUserAccount = database.accountManager.currentUserAccount
  }

  /**
   * Set [currentUserAccount] to the specified value
   *
   * Warning: no checks are made on the specified account, do not provide an invalid account
   */
  fun signInTo(account: ChimpagneAccount) {
    currentUserAccount = account
  }

  /** Set [currentUserAccount] to null */
  fun signOut() {
    currentUserAccount = null
  }

  /**
   * Retrieve an account from Firebase with the specified Firebase Auth Id
   *
   * @param uid a Firebase User UID (see
   *   https://console.firebase.google.com/u/0/project/chimpagneapp/authentication/users)
   * @param onSuccess(account) Called when the request is successful. Warning: account could be null
   *   if there is no account associated with the given id
   * @param onFailure(exception) Called in case of... failure
   */
  fun getAccount(
      uid: ChimpagneAccountUID,
      onSuccess: (ChimpagneAccount?) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    accounts
        .document(uid)
        .get()
        .addOnSuccessListener { onSuccess(it.toObject<ChimpagneAccount>()) }
        .addOnFailureListener { onFailure(it) }
  }

  fun getAccountWithProfilePicture(
      uid: ChimpagneAccountUID,
      onSuccess: (ChimpagneAccount?, Uri?) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    getAccount(
        uid,
        { account ->
          if (account == null) onSuccess(null, null)
          else
              downloadProfilePicture(
                  account.firebaseAuthUID,
              ) { uri ->
                onSuccess(account, uri)
              }
        },
        onFailure)
  }

  /** Puts the given account to Firebase and updates [currentUserAccount] accordingly */
  fun updateCurrentAccount(
      account: ChimpagneAccount,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    accounts
        .document(account.firebaseAuthUID)
        .set(account)
        .addOnSuccessListener {
          currentUserAccount = account
          onSuccess()
        }
        .addOnFailureListener { onFailure(it) }
  }

  fun updateCurrentAccount(
      account: ChimpagneAccount,
      profilePicture: Uri?,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    if (profilePicture == null) {
      updateCurrentAccount(account, onSuccess, onFailure)
    } else {
      uploadProfilePicture(
          account,
          profilePicture,
          { updateCurrentAccount(account, onSuccess, onFailure) },
          onFailure)
    }
  }

  private fun uploadProfilePicture(
      account: ChimpagneAccount,
      uri: Uri,
      onSuccess: (String) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    //    profilePictures.child(account.firebaseAuthUID).delete()
    val imageRef = profilePictures.child(account.firebaseAuthUID)
    imageRef
        .putFile(uri)
        .addOnSuccessListener {
          imageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
            Log.d("ChimpagneAccountManager", "Uploaded image to: $downloadUrl")
            onSuccess(downloadUrl.toString())
          }
        }
        .addOnFailureListener { onFailure(it) }
  }

  private fun downloadProfilePicture(uid: String, onSuccess: (Uri?) -> Unit) {
    profilePictures
        .child(uid)
        .downloadUrl
        .addOnSuccessListener { downloadedURI -> onSuccess(downloadedURI) }
        .addOnFailureListener { onSuccess(null) }
  }

  private val eventManager = database.eventManager

  /** @param role: ChimpagneRole (for instance ChimpagneRoles.GUEST) */
  fun joinEvent(
      id: ChimpagneEventId,
      role: Int,
      onSuccess: () -> Unit = {},
      onFailure: (Exception) -> Unit = {}
  ) {
    if (currentUserAccount == null) {
      onFailure(NotLoggedInException())
      return
    }

    val updatedAccount =
        currentUserAccount!!.copy(joinedEvents = currentUserAccount!!.joinedEvents + (id to true))
    when (role) {
      ChimpagneRoles.GUEST ->
          eventManager.addGuest(
              id,
              updatedAccount.firebaseAuthUID,
              { updateCurrentAccount(updatedAccount, onSuccess, onFailure) },
              onFailure)
      ChimpagneRoles.STAFF ->
          eventManager.addStaff(
              id,
              updatedAccount.firebaseAuthUID,
              { updateCurrentAccount(updatedAccount, onSuccess, onFailure) },
              onFailure)
      else -> updateCurrentAccount(updatedAccount, onSuccess, onFailure)
    }
  }

  fun leaveEvent(
      id: ChimpagneEventId,
      onSuccess: () -> Unit = {},
      onFailure: (Exception) -> Unit = {}
  ) {
    if (currentUserAccount == null) {
      onFailure(NotLoggedInException())
      return
    }

    val updatedAccount =
        currentUserAccount!!.copy(joinedEvents = currentUserAccount!!.joinedEvents - id)

    eventManager.removeGuest(
        id,
        updatedAccount.firebaseAuthUID,
        {
          eventManager.removeStaff(
              id,
              updatedAccount.firebaseAuthUID,
              { updateCurrentAccount(updatedAccount, onSuccess, onFailure) },
              onFailure)
        },
        onFailure)
  }
}

class NotLoggedInException : Exception("Not logged in")
