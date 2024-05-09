package com.monkeyteam.chimpagne.ui.utilities

import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.monkeyteam.chimpagne.R
import com.monkeyteam.chimpagne.ui.components.IconTextButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

const val DEEP_LINK_URI = "https://www.manigo.ch/events/?uid="

@Composable
fun DisplayQRCode(eventId: String) {
  var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }

  val size = calculateSize()

  LaunchedEffect(eventId) { imageBitmap = generateQRCodeBitmap(eventId, size) }

  Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
    if (imageBitmap != null) {
      Image(
          bitmap = imageBitmap!!,
          contentDescription = "Event QR Code",
          modifier = Modifier.fillMaxWidth().testTag("qr_code_image"),
          contentScale = ContentScale.FillWidth)
    } else {
      CircularProgressIndicator(
          modifier = Modifier.size(50.dp).testTag("loading")) // Adjust the size as needed
    }
  }
}

@Composable
fun calculateSize(): Int {
  val configuration = LocalConfiguration.current
  val screenWidth = configuration.screenWidthDp.dp
  return with(LocalDensity.current) { screenWidth.toPx().toInt() }
}

suspend fun generateQRCodeBitmap(data: String, size: Int): ImageBitmap {
  return withContext(Dispatchers.Default) {
    val qrCodeWriter = QRCodeWriter()
    val bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, size, size)
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)
    for (x in 0 until size) {
      for (y in 0 until size) {
        bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
      }
    }
    bitmap.asImageBitmap()
  }
}

@Composable
fun QRCodeDialog(eventId: String, onDismiss: () -> Unit) {

  Dialog(onDismissRequest = onDismiss) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.padding(16.dp),
        color = MaterialTheme.colorScheme.surface) {
          Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              modifier = Modifier.padding(all = 16.dp)) {
                DisplayQRCode(eventId = DEEP_LINK_URI + eventId)
                Spacer(modifier = Modifier.height(16.dp))
                IconTextButton(
                    text = stringResource(id = R.string.close),
                    icon = Icons.Rounded.Close,
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_button"))
              }
        }
  }
}
