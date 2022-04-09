package it.dani.cameraapp.camera

import android.annotation.SuppressLint
import android.util.Log
import androidx.camera.core.ImageProxy
import com.google.mlkit.common.model.LocalModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.custom.CustomObjectDetectorOptions

/**
 * @author Daniele
 *
 * This class extends [ObjectDetection], is dedicated to detect eye on an image
 */

class EyeTrackingDetector : ObjectDetection() {

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

                    val detectedObjects : MutableList<DetectedObject> = ArrayList()
                    l.forEachIndexed { i,b ->
                        val boundingBox = BoundingBox((b.boundingBox.left*1.0f)/image.width,
                            (b.boundingBox.top*1.0f)/image.height,
                            (b.boundingBox.right*1.0f)/image.width,
                            (b.boundingBox.bottom*1.0f)/image.height)
                        detectedObjects += DetectedObject(boundingBox,b.trackingId ?: i,b.labels)
                    }
                    this@EyeTrackingDetector.onSuccess.forEach { it(detectedObjects) }
                    this@EyeTrackingDetector.onGiveImageSize.forEach { it(inputImage.width,inputImage.height) }
                }
                addOnCompleteListener {
                    img.close()
                    image.close()
                }
            }
        }
    }
}