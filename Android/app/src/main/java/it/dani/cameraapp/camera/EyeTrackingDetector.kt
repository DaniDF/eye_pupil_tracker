package it.dani.cameraapp.camera

import android.annotation.SuppressLint
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ImageProxy
import com.google.mlkit.common.model.LocalModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.custom.CustomObjectDetectorOptions

class EyeTrackingDetector(private val context: AppCompatActivity) : ObjectDetection() {

    private val localModel = LocalModel.Builder().setAssetFilePath("eye_tracking.tflite").build()
    private val customObjectDetectorOptions = CustomObjectDetectorOptions.Builder(this.localModel).apply {
        setDetectorMode(CustomObjectDetectorOptions.STREAM_MODE)
        setClassificationConfidenceThreshold(0.01f)
        setMaxPerObjectLabelCount(5)
    }.build()
    private val objectDetector = com.google.mlkit.vision.objects.ObjectDetection.getClient(this.customObjectDetectorOptions)

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(image: ImageProxy) {

        image.image?.let{ img ->
            val inputImage = InputImage.fromMediaImage(img,image.imageInfo.rotationDegrees)

            this.objectDetector.process(inputImage).apply {
                addOnFailureListener { e ->
                    Log.e("ANALYZER","Error: ${e.message}")
                    e.printStackTrace()
                }
                addOnSuccessListener { l ->
                    this@EyeTrackingDetector.context.runOnUiThread {
                        this@EyeTrackingDetector.onSuccess(l)
                    }
                }
                addOnCompleteListener {
                    img.close()
                    image.close()
                }
            }
        }
    }
}