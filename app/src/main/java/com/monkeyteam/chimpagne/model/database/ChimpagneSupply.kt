package com.monkeyteam.chimpagne.model.database

import java.util.UUID

typealias ChimpagneSupplyId = String

data class ChimpagneSupply(
    var id: ChimpagneSupplyId = UUID.randomUUID().toString(),
    val description: String = "",
    val quantity: Int = 0,
    val unit: String = "",
    // guest: <is_Assigned_this_grocery>
    val assignedTo: Map<String, Boolean> = hashMapOf(),
) {

  fun assignedList(): Set<String> {
    return assignedTo.keys
  }

  override fun toString(): String {
    // e.g. 6 eggs: descriptions="eggs", quantity="6", unit=""
    if (unit.isEmpty()) {
      return "$quantity $description"
    }
    return "$description $quantity $unit"
  }
}
