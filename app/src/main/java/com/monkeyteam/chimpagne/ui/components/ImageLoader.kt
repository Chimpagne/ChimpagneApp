package com.monkeyteam.chimpagne.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.monkeyteam.chimpagne.R

@Composable
fun ImageLoader(imageUri: String? = null, overlay: Boolean = false) {
  Box(modifier = Modifier.fillMaxSize()) {
    val context = LocalContext.current
    val request =
        ImageRequest.Builder(context)
            .data(imageUri ?: R.drawable.chimpagne_app_logo)
            .placeholder(R.drawable.chimpagne_app_logo)
            .error(R.drawable.chimpagne_app_logo)
            .build()

    AsyncImage(
        model = request,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize())
    if (overlay) {
      Box(modifier = Modifier.matchParentSize().background(Color.Black.copy(alpha = 0.3f)))
    }
  }
}
