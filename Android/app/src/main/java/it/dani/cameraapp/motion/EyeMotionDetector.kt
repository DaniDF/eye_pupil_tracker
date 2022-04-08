package it.dani.cameraapp.motion

import android.util.Log
import com.google.mlkit.vision.objects.DetectedObject
import it.dani.cameraapp.camera.ObjectDetection

/**
 * @author Daniele
 *
 * This class wrap an [ObjectDetection] used for detect the eye movements
 *
 * @param[objectDetection] The object detection object
 */

class EyeMotionDetector(objectDetection: ObjectDetection) {

    /**
     * @property[onEyeLeft] List of handler fired when eyes move to left
     */
    val onEyeLeft : MutableList<(Pair<Int,Int>,Pair<Int,Int>) -> Any> = ArrayList()

    /**
     * @property[onEyeRight] List of handler fired when eyes move to right
     */
    val onEyeRight : MutableList<(Pair<Int,Int>,Pair<Int,Int>) -> Any> = ArrayList()

    /**
     * @property[onEyeUp] List of handler fired when eyes move to up
     */
    val onEyeUp : MutableList<(Pair<Int,Int>,Pair<Int,Int>) -> Any> = ArrayList()

    /**
     * @property[onEyeDown] List of handler fired when eyes move to down
     */
    val onEyeDown : MutableList<(Pair<Int,Int>,Pair<Int,Int>) -> Any> = ArrayList()

    /**
     * @property[leftEye] Position of last detected left eye
     */
    private lateinit var leftEye : Pair<Int,Int>

    /**
     * @property[rightEye] Position of last detected right eye
     */
    private lateinit var rightEye : Pair<Int,Int>

    init {
        objectDetection.onSuccess += {
            try {
                if(!this::leftEye.isInitialized) {
                    this.leftEye = this.findLeftEye(it)
                }

                if(!this::rightEye.isInitialized) {
                    this.rightEye = this.findRightEye(it)
                }

                val previousLeftEye = this.leftEye
                this.leftEye = this.findLeftEye(it)

                val previousRightEye = this.rightEye
                this.rightEye = this.findRightEye(it)

                this.detectMotion(previousLeftEye,this.leftEye, previousRightEye, this.rightEye)
            } catch (e : IllegalEyeDetectionException) {
                Log.d("Eye_calibration","no eye detected")
            }
        }
    }

    private fun detectMotion(prevLeftEye : Pair<Int,Int>, leftEye : Pair<Int,Int>, prevRightEye : Pair<Int,Int>, rightEye : Pair<Int,Int>) {
        if(prevLeftEye.first + RADIUS < leftEye.first && prevRightEye.first + RADIUS < rightEye.first) {
            this.onEyeRight.forEach { it(leftEye,rightEye) }
        }

        if(prevLeftEye.first + RADIUS > leftEye.first && prevRightEye.first + RADIUS > rightEye.first) {
            this.onEyeLeft.forEach { it(leftEye,rightEye) }
        }

        if(prevLeftEye.second + RADIUS < leftEye.second && prevRightEye.second + RADIUS < rightEye.second) {
            this.onEyeDown.forEach { it(leftEye,rightEye) }
        }

        if(prevLeftEye.second + RADIUS > leftEye.second && prevRightEye.second + RADIUS > rightEye.second) {
            this.onEyeUp.forEach { it(leftEye,rightEye) }
        }
    }

    /**
     * This method finds the most left eye on a list on [eyes]
     *
     * @param[eyes] A list of detected eyes
     */
    private fun findLeftEye(eyes : List<DetectedObject>) : Pair<Int,Int> {
        if(eyes.isEmpty()) {
            throw IllegalEyeDetectionException("Error: no eye provided")
        }

        var result = eyes[0].boundingBox.left to eyes[0].boundingBox.top
        eyes.forEach {
            if(it.boundingBox.left < result.first) {
                result = it.boundingBox.left to it.boundingBox.top
            }
        }

        return result
    }

    /**
     * This method finds the most right eye on a list on [eyes]
     *
     * @param[eyes] A list of detected eyes
     */
    private fun findRightEye(eyes : List<DetectedObject>) : Pair<Int,Int> {
        if(eyes.isEmpty()) {
            throw IllegalEyeDetectionException("Error: no eye provided")
        }

        var result = eyes[0].boundingBox.left to eyes[0].boundingBox.top
        eyes.forEach {
            if(it.boundingBox.left > result.first) {
                result = it.boundingBox.left to it.boundingBox.top
            }
        }

        return result
    }

    companion object {

        /**
         * @property[RADIUS] The confidence interval for a movement
         */
        private const val RADIUS = 50  //TODO
    }
}