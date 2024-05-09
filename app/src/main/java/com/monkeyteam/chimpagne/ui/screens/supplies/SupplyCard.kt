package com.monkeyteam.chimpagne.ui.screens.supplies

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.monkeyteam.chimpagne.model.database.ChimpagneSupply

@Composable
fun SupplyCard(supply: ChimpagneSupply, modifier: Modifier = Modifier, onClick: () -> Unit) {
  Card(
      modifier =
          modifier
              .clickable { onClick() }
              .padding(horizontal = 16.dp, vertical = 8.dp)
              .fillMaxWidth(),
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primary)) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth().padding(16.dp)) {
              Text(
                  text = "${supply.quantity} ${supply.unit}",
                  modifier = Modifier.testTag("supply_quantity_and_unit"))
              Text(
                  text = "${supply.assignedTo.keys.size} assigned",
                  modifier = Modifier.testTag("supply_nb_assigned"))
            }
      }
}
