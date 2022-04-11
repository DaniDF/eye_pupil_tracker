package it.dani.cameraapp.motion

import android.util.Log
import it.dani.cameraapp.camera.DetectedObject
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
    val onEyeLeft : MutableList<(Pair<Float,Float>,Pair<Float,Float>) -> Any> = ArrayList()

    /**
     * @property[onEyeRight] List of handler fired when eyes move to right
     */
    val onEyeRight : MutableList<(Pair<Float,Float>,Pair<Float,Float>) -> Any> = ArrayList()

    /**
     * @property[onEyeUp] List of handler fired when eyes move to up
     */
    val onEyeUp : MutableList<(Pair<Float,Float>,Pair<Float,Float>) -> Any> = ArrayList()

    /**
     * @property[onEyeDown] List of handler fired when eyes move to down
     */
    val onEyeDown : MutableList<(Pair<Float,Float>,Pair<Float,Float>) -> Any> = ArrayList()

    /**
     * @property[leftEye] Position of last detected left eye
     */
    private lateinit var leftEye : Pair<Float,Float>

    /**
     * @property[rightEye] Position of last detected right eye
     */
    private lateinit var rightEye : Pair<Float,Float>

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

    private fun detectMotion(prevLeftEye : Pair<Float,Float>, leftEye : Pair<Float,Float>, prevRightEye : Pair<Float,Float>, rightEye : Pair<Float,Float>) {
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
    private fun findLeftEye(eyes : List<DetectedObject>) : Pair<Float,Float> {
        if(eyes.isEmpty()) {
            throw IllegalEyeDetectionException("Error: no eye provided")
        }

        var result = eyes[0].boundingBox.left to eyes[0].boundingBox.top
        eyes.forEach {
            if(it.boundingBox.left < result.first) {
                result = (it.boundingBox.left + it.boundingBox.right)/2 to (it.boundingBox.top + it.boundingBox.bottom)/2
            }
        }

        return result
    }

    /**
     * This method finds the most right eye on a list on [eyes]
     *
     * @param[eyes] A list of detected eyes
     */
    private fun findRightEye(eyes : List<DetectedObject>) : Pair<Float,Float> {
        if(eyes.isEmpty()) {
            throw IllegalEyeDetectionException("Error: no eye provided")
        }

        var result = eyes[0].boundingBox.left to eyes[0].boundingBox.top
        eyes.forEach {
            if(it.boundingBox.left > result.first) {
                result = (it.boundingBox.left + it.boundingBox.right)/2 to (it.boundingBox.top + it.boundingBox.bottom)/2
            }
        }

        return result
    }

    companion object {

        /**
         * @property[RADIUS] The confidence interval for a movement
         */
        private const val RADIUS = 0.05f
    }
}