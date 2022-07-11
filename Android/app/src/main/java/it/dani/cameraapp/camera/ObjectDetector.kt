package it.dani.cameraapp.camera

import android.graphics.Bitmap
import androidx.camera.core.ImageAnalysis
import org.tensorflow.lite.support.label.Category

/**
 * @author Daniele
 *
 * This class represents object detection classes
 */

abstract class ObjectDetector : ImageAnalysis.Analyzer {
    /**
     * @property[onSuccess] List of handlers on, they will be fired on successfully detection
     */
    val onSuccess : MutableList<(bitmap : Bitmap, List<DetectedObject>) -> Any> = ArrayList()

    /**
     * @property[onGiveImageSize] List of handlers for deliver the current image (sensor) dimensions
     */
    val onGiveImageSize : MutableList<(Int,Int) -> Any> = ArrayList()

    /**
     * @property[accuracyThreshold] Threshold for detections' accuracy, under will not given
     */
    var accuracyThreshold = ACCURACY_THRESHOLD

    /**
     * This method compute the incoming bitmap and returns a list of [DetectedObject]
     *
     * @param[bitmap] The bitmap image to analyze
     * @return A list of [DetectedObject] containing the detected object in the bitmap
     */
    abstract fun analyze(bitmap : Bitmap) : List<DetectedObject>

    companion object {
        /**
         * @property[ACCURACY_THRESHOLD] Default threshold for detections' accuracy, under will not given
         */
        private const val ACCURACY_THRESHOLD = 0.1f
    }
}

/**
 * This class represents a detected object
 *
 * @param[boundingBox] A rectangle that surround the detected object
 * @param[labels] A list of labels (see [Category]) that's describe what is in the box
 * @param[trackingId] An id of the detection
 */
data class DetectedObject(val boundingBox : BoundingBox, val trackingId : Int, val labels : List<Category>)


/**
 * This function adds a method to a [List] of [Category] to calculate the average score based on the score of single label
 */
fun List<Category>.averageScore() : Float {
    var result = 0f

    this.forEach { result += it.score }

    return result / this.size
}

/**
 * This class represents a bounding box rectangle
 *
 * @param[left] The percentage of left edge, values between 0 to 1
 * @param[top] The percentage of top edge, values between 0 to 1
 * @param[right] The percentage of right edge, values between 0 to 1
 * @param[bottom] The percentage of bottom edge, values between 0 to 1
 */
data class BoundingBox(val left : Float, val top : Float, val right : Float, val bottom : Float)