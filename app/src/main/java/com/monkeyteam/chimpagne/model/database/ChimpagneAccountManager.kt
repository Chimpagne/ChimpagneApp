package com.monkeyteam.chimpagne.model.database

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.toObject

/** Use this class to interact */
class ChimpagneAccountManager(private val accounts: CollectionReference) {

  /**
   * This field stores the current logged user's account, you can retrieve it from any class using
   *
   * @sample currentUserAccountSample
   */
  var currentUserAccount: ChimpagneAccount? = null
    private set

  private fun currentUserAccountSample() {
    // Use this to get the current logged user
    val currentUserAccount = Database.instance.accountManager.currentUserAccount
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
   * Retrieve an account from Firebase with the specified email
   *
   * @param email It's in the name
   * @param onSuccess(account) Called when the request is successful. Warning: account could be null
   *   if there is no account associated with the given email
   * @param onFailure(exception) Called in case of... failure
   */
  fun getAccountByEmail(
      email: String,
      onSuccess: (ChimpagneAccount?) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    accounts
        .document(email)
        .get()
        .addOnSuccessListener { onSuccess(it.toObject<ChimpagneAccount>()) }
        .addOnFailureListener { onFailure(it) }
  }

  /** Puts the given account to Firebase and updates [currentUserAccount] accordingly */
  fun updateCurrentAccount(
      account: ChimpagneAccount,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    if (currentUserAccount != null && account.email != currentUserAccount!!.email) {
      accounts.document(currentUserAccount!!.email).delete()
    }

    accounts
        .document(account.email)
        .set(account)
        .addOnSuccessListener {
          currentUserAccount = account
          onSuccess()
        }
        .addOnFailureListener { onFailure(it) }
  }
}
