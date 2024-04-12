package com.monkeyteam.chimpagne.ui.components

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

// test class so that it compiles with my code, won't actually be here and probably will have
// more attributes
data class User(
    val id: String,
    val name: String,
    // Nullable for users that didn't specify a profile picture
    val profilePictureURL: String? = null
)

@Composable
fun ProfileIcon(user: User?, onClick: () -> Unit = {}) {
  val painter =
      if (user?.profilePictureURL != null) {
        rememberAsyncImagePainter(model = user.profilePictureURL)
      } else painterResource(id = R.drawable.default_user_profile_picture)

  IconButton(onClick = onClick) {
    Image(
        painter = painter,
        contentDescription = "Profile",
        modifier = Modifier.size(40.dp).clip(CircleShape),
        contentScale = ContentScale.Crop)
  }
}
