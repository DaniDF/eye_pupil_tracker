package it.dani.cameraapp.camera

import android.annotation.SuppressLint
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ImageProxy
import com.google.mlkit.common.model.LocalModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.custom.CustomObjectDetectorOptions

/**
 * @author Daniele
 *
 * This class extends [ObjectDetection], is dedicated to detect eye on an image
 */

class EyeTrackingDetector(private val context: AppCompatActivity) : ObjectDetection() {

    /**
     * @property[localModel] The Tensorflow lite model used for detections
     */
    private val localModel = LocalModel.Builder().setAssetFilePath("eye_tracking.tflite").build()

    /**
     * @property[customObjectDetectorOptions] Specific options for detections
     */
    private val customObjectDetectorOptions = CustomObjectDetectorOptions.Builder(this.localModel).apply {
        setDetectorMode(CustomObjectDetectorOptions.STREAM_MODE)
        enableMultipleObjects()
        setClassificationConfidenceThreshold(0.9f)
        setMaxPerObjectLabelCount(5)
    }.build()

    /**
     * @property[objectDetector] The object detector object
     */
    private val objectDetector = com.google.mlkit.vision.objects.ObjectDetection.getClient(this.customObjectDetectorOptions)


    /**
     * This method compute the incoming image and fires the [onSuccess] handlers
     *
     * @param[image] The image proxy that's contains the captured image
     */
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
                    Log.d("ANALYZER","Found: something [objects: ${l.size}]")
                    this@EyeTrackingDetector.context.runOnUiThread {
                        this@EyeTrackingDetector.onSuccess.forEach { it(l) }
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