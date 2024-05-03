package com.monkeyteam.chimpagne.ui.utilities

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.monkeyteam.chimpagne.R
import com.monkeyteam.chimpagne.ui.components.IconTextButton
import java.util.concurrent.Executors

class QRCodeAnalyser(val callback: (String) -> Unit) : ImageAnalysis.Analyzer {
  @OptIn(ExperimentalGetImage::class)
  override fun analyze(imageProxy: ImageProxy) {
    val options = BarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_QR_CODE).build()
    val scanner = BarcodeScanning.getClient(options)
    val mediaImage = imageProxy.image
    mediaImage?.let {
      val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
      scanner.process(image).addOnSuccessListener { barcodes ->
        if (barcodes.size > 0) {
          callback(barcodes[0].displayValue.toString())
        }
      }
    }
    imageProxy.close()
  }
}

@Composable
fun CameraPreview(modifier: Modifier, onResult: (String) -> Unit) {
  AndroidView(
      { context ->
        val cameraExecutor = Executors.newSingleThreadExecutor()
        val previewView =
            PreviewView(context).also { it.scaleType = PreviewView.ScaleType.FILL_CENTER }
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener(
            {
              val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

              val preview =
                  Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                  }

              val imageCapture = ImageCapture.Builder().build()

              val imageAnalyzer =
                  ImageAnalysis.Builder().build().also { it ->
                    it.setAnalyzer(cameraExecutor, QRCodeAnalyser { onResult(it) })
                  }

              val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

              try {
                cameraProvider.unbindAll()

                cameraProvider.bindToLifecycle(
                    context as ComponentActivity,
                    cameraSelector,
                    preview,
                    imageCapture,
                    imageAnalyzer)
              } catch (exc: Exception) {
                Log.e("DEBUG", "Use case binding failed", exc)
              }
            },
            ContextCompat.getMainExecutor(context))
        previewView
      },
      modifier = modifier.testTag("camera_preview"))
}

@Composable
fun QRCodeScanner(close: () -> Unit, onResult: (String) -> Unit) {
  Box(modifier = Modifier.fillMaxSize().testTag("qr_code_scanner")) {
    CameraPreview(modifier = Modifier.matchParentSize(), onResult)

    IconTextButton(
        onClick = { close() },
        icon = Icons.Rounded.Close,
        text = stringResource(id = R.string.close),
        modifier =
            Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp).testTag("close_button"))
  }
}
