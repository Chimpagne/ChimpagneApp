package com.monkeyteam.chimpagne.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil.compose.rememberAsyncImagePainter
import com.monkeyteam.chimpagne.R

@Composable
fun ImageWithBlackFilterOverlay(imageString: String = "", overlay: Boolean = false) {
  Box(modifier = Modifier.fillMaxWidth()) {
    if (imageString.isEmpty()) {
      Image(
          painter = painterResource(id = R.drawable.chimpagne_app_logo),
          contentDescription = "Default Logo",
          contentScale = ContentScale.FillWidth,
          modifier = Modifier.fillMaxWidth())
    } else {
      Image(
          painter = rememberAsyncImagePainter(model = imageString),
          contentDescription = "Event Image",
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize())
    }
    if (!overlay) return
    Box(modifier = Modifier.matchParentSize().background(Color.Black.copy(alpha = 0.5f)))
  }
}
