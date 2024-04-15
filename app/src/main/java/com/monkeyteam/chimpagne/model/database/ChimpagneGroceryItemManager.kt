package com.monkeyteam.chimpagne.model.database
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.toObject
class ChimpagneGroceryItemManager(private val groceries: CollectionReference) {
    fun getGroceryItemById(
        id: String,
        onSuccess: (ChimpagneGroceryItem?) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        groceries
            .document(id)
            .get()
            .addOnSuccessListener { onSuccess(it.toObject<ChimpagneGroceryItem>()) }
            .addOnFailureListener { onFailure(it) }
    }
    fun deleteGroceryItem(id: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        groceries
            .document(id)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun updateGroceryItem(
        groceryItem: ChimpagneGroceryItem,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        groceries
            .document(groceryItem.id)
            .set(groceryItem)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }
}