package it.dani.cameraapp.camera

import androidx.camera.core.ImageAnalysis
import com.google.mlkit.vision.objects.DetectedObject

/**
 * @author Daniele
 *
 * This class represents object detection classes
 */

abstract class ObjectDetection : ImageAnalysis.Analyzer {
    /**
     * @property[onSuccess] list of handlers on, they will be fired on successfully detection
     */
    val onSuccess : MutableList<(List<DetectedObject>) -> Any> = ArrayList()
}