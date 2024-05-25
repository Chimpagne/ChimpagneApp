package com.monkeyteam.chimpagne.ui.components.eventview

import android.util.Log
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.monkeyteam.chimpagne.ui.components.ImageWithBlackFilterOverlay

@Composable
fun ImageCard(imageUri: String?) {
  Log.d("AdvancedLogisticsPanel", "Event picture URI is: $imageUri")
  Card(
      modifier = Modifier.padding(16.dp).fillMaxWidth().aspectRatio(1.9f),
      shape = RoundedCornerShape(12.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
      elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
        ImageWithBlackFilterOverlay(imageUri)
      }
}
