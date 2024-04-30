package com.monkeyteam.chimpagne.ui.components

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.monkeyteam.chimpagne.R

@Composable
fun ProfileIcon(uri: Uri?, onClick: () -> Unit = {}) {
  val painter =
      if (uri != null) {
        rememberAsyncImagePainter(model = uri)
      } else painterResource(id = R.drawable.default_user_profile_picture)

  IconButton(onClick = onClick) {
    Image(
        painter = painter,
        contentDescription = "Profile",
        modifier = Modifier.size(40.dp).clip(CircleShape),
        contentScale = ContentScale.Crop)
  }
}
