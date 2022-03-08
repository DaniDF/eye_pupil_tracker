package it.dani.cameraapp.camera

import androidx.camera.core.ImageAnalysis
import com.google.mlkit.vision.objects.DetectedObject

abstract class ObjectDetection : ImageAnalysis.Analyzer {
    var onSuccess : (List<DetectedObject>) -> Unit = {}
}