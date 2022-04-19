package it.dani.cameraapp.camera

import androidx.camera.core.ImageAnalysis
import org.tensorflow.lite.support.label.Category

/**
 * @author Daniele
 *
 * This class represents object detection classes
 */

abstract class ObjectDetection : ImageAnalysis.Analyzer {
    /**
     * @property[onSuccess] List of handlers on, they will be fired on successfully detection
     */
    val onSuccess : MutableList<(List<DetectedObject>) -> Any> = ArrayList()

    /**
     * @property[onGiveImageSize] List of handlers for deliver the current image (sensor) dimensions
     */
    val onGiveImageSize : MutableList<(Int,Int) -> Any> = ArrayList()
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
 * This class represents a bounding box rectangle
 *
 * @param[left] The percentage of left edge, values between 0 to 1
 * @param[top] The percentage of top edge, values between 0 to 1
 * @param[right] The percentage of right edge, values between 0 to 1
 * @param[bottom] The percentage of bottom edge, values between 0 to 1
 */
data class BoundingBox(val left : Float, val top : Float, val right : Float, val bottom : Float)