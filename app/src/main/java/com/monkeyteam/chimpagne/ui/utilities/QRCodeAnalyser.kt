package com.monkeyteam.chimpagne.ui.utilities

import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors

class QRCodeAnalyser(val callback: () -> Unit) : ImageAnalysis.Analyzer {
  @OptIn(ExperimentalGetImage::class)
  override fun analyze(imageProxy: ImageProxy) {
    val options = BarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_QR_CODE).build()

    val scanner = BarcodeScanning.getClient(options)
    val mediaImage = imageProxy.image
    mediaImage?.let {
      val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

      scanner
          .process(image)
          .addOnSuccessListener { barcodes ->
            if (barcodes.size > 0) {
              callback()
            }
          }
          .addOnFailureListener {
            // Task failed with an exception
          }
    }
    imageProxy.close()
  }
}

@Composable
fun QRCodePreview() {
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
                  ImageAnalysis.Builder().build().also {
                    it.setAnalyzer(
                        cameraExecutor,
                        QRCodeAnalyser {
                          Toast.makeText(context, "Barcode found", Toast.LENGTH_SHORT).show()
                        })
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
      modifier = Modifier.size(width = 250.dp, height = 250.dp))
}
