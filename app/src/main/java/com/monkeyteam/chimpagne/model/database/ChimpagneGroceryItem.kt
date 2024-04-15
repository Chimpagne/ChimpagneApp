package com.monkeyteam.chimpagne.model.database

data class ChimpagneGroceryItem(
    val id: String = "",
    val description: String = "",
    val quantity: Float = 0.0f,
    val unit: String = "",
    // guest: <is_Assigned_this_grocery>
    val assignedTo: Map<String, Boolean> = hashMapOf(),
) {

  fun assignedList(): Set<String> {
    return assignedTo.keys
  }

  override fun toString(): String {
      // e.g. 6 eggs: descriptions="eggs", quantity="6", unit=""
      if (unit == ""){
          return "$quantity $description"
      }
        return "$description $quantity $unit"
  }
}
