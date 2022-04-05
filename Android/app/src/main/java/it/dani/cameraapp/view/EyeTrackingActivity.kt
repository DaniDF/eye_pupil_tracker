package it.dani.cameraapp.view

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.*
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.mlkit.vision.objects.DetectedObject
import it.dani.cameraapp.R
import it.dani.cameraapp.camera.EyeTrackingDetector
import it.dani.cameraapp.camera.ObjectDetection
import it.dani.cameraapp.view.utils.PermissionUtils
import java.lang.StringBuilder

/**
 * @author Daniele
 *
 * This class display what camera is viewing
 */

class EyeTrackingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.eye_tracking_activity)

        if(!PermissionUtils.permissionGranted(this, arrayOf(Manifest.permission.CAMERA))) {
            ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.CAMERA),0xA1)
        } else {
            this.provideCamera()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode) {
            0xA1 -> {
                var flagOK = true
                for(countRes in grantResults.indices) {
                    if(grantResults[countRes] != PackageManager.PERMISSION_GRANTED) {
                        flagOK = false
                    }
                }

                if(flagOK) {
                    this.provideCamera()
                } else {
                    Snackbar.make(this.findViewById(R.id.main_view),
                        R.string.permission_camera_denied,
                        Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * This method attach listeners to camera
     */
    private fun provideCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            this.bindPreview(cameraProviderFuture.get())
        }, ContextCompat.getMainExecutor(this))
    }

    /**
     * This method remove all camera uses and close the camera
     */
    private fun bindPreview(cameraProvider: ProcessCameraProvider) {

        val previewView = this.findViewById<PreviewView>(R.id.preview_view)

        var isPreviewActive = false
        var isAnalyzeActive = false
        var cameraSelected = CameraSelector.LENS_FACING_FRONT

        var cameraSelector = CameraSelector.Builder()
            .requireLensFacing(cameraSelected)
            .build()

        val preview = Preview.Builder().build()
        preview.setSurfaceProvider(previewView.surfaceProvider)

        val analysis = ImageAnalysis.Builder()
            .setTargetRotation(previewView.display.rotation)
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        val adjustFrontFunc = { rect : Rect -> Rect(previewView.width - rect.left,rect.top,previewView.width - rect.right,rect.bottom) }
        val adjustDflFunc = { rect : Rect -> rect}
        var adjustFunc = when(cameraSelected) {
            CameraSelector.LENS_FACING_FRONT -> adjustFrontFunc
            else -> adjustDflFunc
        }

        val analyzer : ObjectDetection = EyeTrackingDetector(this).apply {
            onSuccess += { this@EyeTrackingActivity.manageAnalyzedObjs(it,adjustFunc) }
        }

        findViewById<Button>(R.id.preview_button).apply {
            setOnClickListener {
                isPreviewActive = !isPreviewActive
                val buttonText = if(isPreviewActive) R.string.preview_image_button_stop else R.string.preview_image_button_start
                this.setText(buttonText)

                if(!isPreviewActive) {
                    isAnalyzeActive = false

                    cameraProvider.unbindAll()
                } else {
                    cameraProvider.bindToLifecycle(this@EyeTrackingActivity as LifecycleOwner,cameraSelector,preview)
                }
            }
        }

        findViewById<Button>(R.id.analyze_button).apply {
            setOnClickListener {
                if(isPreviewActive) {
                    isAnalyzeActive = !isAnalyzeActive
                    val buttonText = if(isAnalyzeActive) R.string.analyze_image_button_stop else R.string.analyze_image_button_start
                    this.setText(buttonText)

                    if(!isAnalyzeActive) {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(this@EyeTrackingActivity as LifecycleOwner,cameraSelector,preview)
                    } else {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(this@EyeTrackingActivity as LifecycleOwner,cameraSelector,preview,analysis)
                    }
                }
            }
        }

        findViewById<FloatingActionButton>(R.id.switch_camera_button).apply {
            setOnClickListener {
                cameraProvider.unbindAll()

                val newCameraSelector = CameraSelector.Builder()
                    .requireLensFacing(
                        when(cameraSelected) {
                            CameraSelector.LENS_FACING_FRONT -> {
                                cameraSelected = CameraSelector.LENS_FACING_BACK
                                adjustFunc = adjustDflFunc
                                cameraSelected
                        }
                        CameraSelector.LENS_FACING_BACK -> {
                            cameraSelected = CameraSelector.LENS_FACING_FRONT
                            adjustFunc = adjustFrontFunc
                            cameraSelected
                        }
                        else -> {
                            cameraSelected = CameraSelector.LENS_FACING_FRONT
                            adjustFunc = adjustFrontFunc
                            cameraSelected
                        }
                    }).build()

                cameraSelector = newCameraSelector
                if(isAnalyzeActive) {
                    cameraProvider.bindToLifecycle(this@EyeTrackingActivity as LifecycleOwner,cameraSelector,preview,analysis)
                } else if(isPreviewActive) {
                    cameraProvider.bindToLifecycle(this@EyeTrackingActivity as LifecycleOwner,cameraSelector,preview)
                }
            }
        }

        analysis.setAnalyzer({ Thread(it).start() },analyzer)

        cameraProvider.unbindAll()
    }

    private fun getAnalyzeColor(i : Int) : Int {
        return when(i%5) {
            0 -> Color.RED
            1 -> Color.BLUE
            2 -> Color.YELLOW
            3 -> Color.WHITE
            else -> Color.GREEN
        }
    }

    /**
     * This method display on a preview view the founds detections
     *
     * @param[objs] A list of detected objects
     * @param[adjustFunc] A function for mirroring the result is currently used camera is front camera
     */
    private fun manageAnalyzedObjs(objs : List<DetectedObject>, adjustFunc : (Rect) -> Rect) {
        val analyzeView = findViewById<ConstraintLayout>(R.id.analyze_view).apply { removeAllViews() }
        val textLabelView = findViewById<LinearLayout>(R.id.text_label_view).apply { removeAllViews() }

        var count = 0
        objs.forEach { obj ->

            Log.d("Detected_OBJ", this.stringObj(obj))

            if(count++ < 5) {

                val width = analyzeView.width
                val height = analyzeView.height

                val bitmap = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888)

                val canvas = Canvas(bitmap)
                val rectF = adjustFunc(obj.boundingBox)
                canvas.drawRect(rectF,Paint().apply {
                    color = this@EyeTrackingActivity.getAnalyzeColor(count)
                    style = Paint.Style.STROKE
                })

                val imageView = ImageView(this@EyeTrackingActivity,).apply {
                    setImageBitmap(bitmap)
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }

                val logVal = StringBuilder("[COUNT_$count ")

                for(ll in obj.labels) {
                    logVal.append("${ll.text} ${ll.confidence}")
                }

                logVal.append("]\n")

                analyzeView.addView(imageView)
                val textView = TextView(this@EyeTrackingActivity).apply {
                    text = logVal.toString()
                    setTextColor(this@EyeTrackingActivity.getAnalyzeColor(count))
                    setTypeface(null, Typeface.BOLD)
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )

                }
                textLabelView.addView(textView)
            }
        }
    }

    private fun stringObj(objs: DetectedObject) : String {
        return "id[${objs.trackingId}] labels[${objs.labels.map { "${it.index},${it.text},${it.confidence}" }}] ${objs.boundingBox}"
    }
}