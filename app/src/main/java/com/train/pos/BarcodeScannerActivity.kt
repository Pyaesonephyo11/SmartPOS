package com.train.pos

import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.jar.Manifest

class BarcodeScannerActivity : AppCompatActivity() {

    private var scanned = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_barcode_scanner)
        val btnCancel=findViewById<Button>(R.id.btnCancel)

        btnCancel.setOnClickListener {
            finish() // scanner activity end
        }

       checkCameraPermission()
    }

    private fun startCamera() {
        val providerFuture = ProcessCameraProvider.getInstance(this)

        providerFuture.addListener({
            val provider = providerFuture.get()

            val preview = Preview.Builder().build()
            val previewView = findViewById<PreviewView>(R.id.previewView)
            preview.setSurfaceProvider(previewView.surfaceProvider)

            val analyzer = ImageAnalysis.Builder().build()
            analyzer.setAnalyzer(
                ContextCompat.getMainExecutor(this),
                BarcodeAnalyzer { code ->
                    if (!scanned) {
                        scanned = true
                       // playScanBeep()
                        returnResult(code)
                    }
                }
            )

            provider.unbindAll()
            provider.bindToLifecycle(
                this,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                analyzer
            )
        }, ContextCompat.getMainExecutor(this))
    }

    private fun returnResult(barcode: String) {

        playScanBeep()

        val intent = Intent()
        intent.putExtra("barcode", barcode)
        setResult(RESULT_OK, intent)
        finish()
    }
    fun playScanBeep() {
        val toneGen = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
        toneGen.startTone(ToneGenerator.TONE_PROP_BEEP, 150)
    }

    private fun checkCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.CAMERA,
                ),
                100
            )
        } else {
            // permission ရပြီးသား → camera start
            startCamera()
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 100) {
            if (grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                // user က Allow လုပ်လိုက်ရင်
                startCamera()
            } else {
                Toast.makeText(
                    this,
                    "Camera permission လိုအပ်ပါတယ်",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

}

