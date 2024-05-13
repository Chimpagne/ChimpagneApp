package com.monkeyteam.chimpagne.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import com.monkeyteam.chimpagne.ui.theme.ChimpagneFontFamily
import com.monkeyteam.chimpagne.ui.theme.ChimpagneTypography
import java.util.Locale

@Composable
fun ChimpagneButton(
    modifier: Modifier = Modifier,
    text: String = "Click Me",
    icon: ImageVector? = null,
    textStyle: TextStyle = ChimpagneTypography.displayMedium,
    onClick: () -> Unit,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    shape: Shape = RoundedCornerShape(12.dp),
    padding: PaddingValues = PaddingValues(horizontal = 18.dp, vertical = 10.dp)
) {
  Button(
      onClick = onClick,
      modifier = modifier.wrapContentWidth().padding(horizontal = 24.dp),
      colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
      shape = shape,
      contentPadding = padding) {
        if (icon != null) {
          Icon(imageVector = icon, contentDescription = "Button icon desc")
          Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
        }
        Text(
            text = text,
            modifier = Modifier.weight(1f),
            fontFamily = ChimpagneFontFamily,
            style = textStyle,
            textAlign = TextAlign.Center)
      }
}

@Composable
fun IconTextButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
  Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier =
          modifier
              .shadow(elevation = 4.dp, shape = RoundedCornerShape(100))
              .background(
                  shape = RoundedCornerShape(100), color = MaterialTheme.colorScheme.surfaceVariant)
              .clickable(onClick = onClick)
              .padding(horizontal = 24.dp, vertical = 12.dp)) {
        Icon(icon, contentDescription = text)
        Spacer(Modifier.width(8.dp))
        Text(text.uppercase(Locale.ROOT))
      }
}

@Composable
fun GoBackButton(navigationActions: NavigationActions) {
  IconButton(onClick = { navigationActions.goBack() }) {
    Icon(
        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
        contentDescription = "Go Back",
        tint = MaterialTheme.colorScheme.onSurface)
  }
}

@Composable
fun SocialButton(imageLogo: Int, urlAsString: String, context: Context, testTag: String) {
  Image(
      painter = painterResource(id = imageLogo),
      contentDescription = "Social Button",
      modifier =
          Modifier.size(55.dp)
              .clickable {
                val urlIntent = Intent(Intent.ACTION_VIEW, Uri.parse(urlAsString))
                context.startActivity(urlIntent)
              }
              .testTag(testTag))
}

@Composable
fun SocialButtonRow(context: Context, socialMediaLinks: Map<String, SocialMedia>) {
  Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(20.dp)) {
        for (socialMedia in socialMediaLinks.values) {
          if (socialMedia.chosenGroupUrl.isNotEmpty()) {
            SocialButton(
                imageLogo = socialMedia.iconResource,
                urlAsString = socialMedia.chosenGroupUrl,
                context = context,
                testTag = socialMedia.testTag)
          }
        }
      }
}
