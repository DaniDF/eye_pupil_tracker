package it.dani.cameraapp.mock

import android.graphics.Rect
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.objects.DetectedObject
import it.dani.cameraapp.camera.ObjectDetection

/**
 * @author Daniele
 *
 * This class emulates the behavior of [EyeTrackingDetector] (only for testing), simulates a sequence of eye movements
 */
class EyeTrackingDetectorMock : ObjectDetection() {

    /**
     * @property[currentPosition] The fake current eye position on the list of emulated movements
     */
    private var currentPosition = 0

    /**
     * @property[oldTime] Used for save the last instant when [analyze] has computed the last image
     */
    private var oldTime = System.currentTimeMillis()

    /**
     * This method emulates the eye detection generating a list of fake eye movements
     *
     * @param[image] The image proxy, unused for this fake detection
     */
    override fun analyze(image: ImageProxy) {

        if(System.currentTimeMillis() - this.oldTime > 5000) {
            this.oldTime = System.currentTimeMillis()

            val rectL = Rect().apply {
                left = POSITIONS[this@EyeTrackingDetectorMock.currentPosition].first
                top = POSITIONS[this@EyeTrackingDetectorMock.currentPosition].second
                right = left + BOX_WIDTH
                bottom = top + BOX_HEIGHT
            }
            val detectedObjectL = DetectedObject(
                rectL,
                this.currentPosition, mutableListOf(DetectedObject.Label("Eye L Mock",1f,1))
            )

            val rectR = Rect().apply {
                left = POSITIONS[this@EyeTrackingDetectorMock.currentPosition].first + BOX_WIDTH
                top = POSITIONS[this@EyeTrackingDetectorMock.currentPosition].second
                right = left + BOX_WIDTH + BOX_WIDTH
                bottom = top + BOX_HEIGHT
            }
            val detectedObjectR = DetectedObject(
                rectR,
                this.currentPosition, mutableListOf(DetectedObject.Label("Eye R Mock",1f,1))
            )

            if(this.currentPosition+1 < POSITIONS.size) {
                this.currentPosition++
            } else {
                this.currentPosition = 0
            }

            super.onSuccess.forEach { it(listOf(detectedObjectL,detectedObjectR)) }
        }

        image.close()
    }

    companion object {
        /**
         * @property[BOX_WIDTH] The bounding box width
         */
        private const val BOX_WIDTH = 100

        /**
         * @property[BOX_HEIGHT] The bounding box height
         */
        private const val BOX_HEIGHT = 100

        /**
         * @property[POSITIONS] The list of fake movements
         */
        private val POSITIONS = listOf(50 to 50, 400 to 50, 400 to 400, 50 to 400)
    }
}