package com.monkeyteam.chimpagne.model.database
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.toObject
class ChimpagneSuppliesManager(private val supplies: CollectionReference) {
    fun getSupplyById(
        id: String,
        onSuccess: (ChimpagneSupply?) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        supplies
            .document(id)
            .get()
            .addOnSuccessListener { onSuccess(it.toObject<ChimpagneSupply>()) }
            .addOnFailureListener { onFailure(it) }
    }
    fun deleteSupply(id: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        supplies
            .document(id)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun registerSupply(
        supply: ChimpagneSupply,
        onSuccess: (id: String) -> Unit,
        onFailure: (Exception) -> Unit,
    ) {
        val eventId = supplies.document().id
        updateSupply(supply.copy(id = eventId), { onSuccess(eventId) }, onFailure)
    }

    fun updateSupply(
        groceryItem: ChimpagneSupply,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        supplies
            .document(groceryItem.id)
            .set(groceryItem)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }
}