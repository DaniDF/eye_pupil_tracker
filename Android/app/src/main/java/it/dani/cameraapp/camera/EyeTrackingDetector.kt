package it.dani.cameraapp.camera

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.camera.core.ImageProxy
import it.dani.cameraapp.camera.ImageUtils.toBitmap
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.vision.detector.ObjectDetector

/**
 * @author Daniele
 *
 * This class extends [ObjectDetection], is dedicated to detect eye on an image
 */

class EyeTrackingDetector(context: Context) : ObjectDetection() {

    /**
     * @property[customObjectDetectorOptions] Specific options for detections
     */
    private val customObjectDetectorOptions = ObjectDetector.ObjectDetectorOptions.builder()
        .setMaxResults(5)
        .setScoreThreshold(this.accuracyThreshold)
        .build()


    /**
     * @property[objectDetector] The object detector object
     */
    private val objectDetector = ObjectDetector.createFromFileAndOptions(context,"converted_model_DFL_metadata_cpy.tflite",this.customObjectDetectorOptions)


    /**
     * This method compute the incoming image and fires the [onSuccess] handlers
     *
     * @param[image] The image proxy that's contains the captured image
     */
    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(image: ImageProxy) {

        image.image?.let{ img ->
            val width = minOf(image.width,image.height)
            val height = maxOf(image.width,image.height)

            val tensorImage = TensorImage.fromBitmap(img.toBitmap())

            val detectedObjects = this.objectDetector.detect(tensorImage)
            Log.d("ANALYZER","Found: something [objects: ${detectedObjects.size}]")

            val result : MutableList<DetectedObject> = ArrayList()
            detectedObjects.forEachIndexed { i,d ->
                val boundingBox = BoundingBox((d.boundingBox.left*1.0f)/width,
                    (d.boundingBox.top*1.0f)/height,
                    (d.boundingBox.right*1.0f)/width,
                    (d.boundingBox.bottom*1.0f)/height)

                result += DetectedObject(boundingBox,i,d.categories)
            }

            this@EyeTrackingDetector.onGiveImageSize.forEach { it(width,height) }
            this@EyeTrackingDetector.onSuccess.forEach { it(result) }

            img.close()
            image.close()
        }
    }
}