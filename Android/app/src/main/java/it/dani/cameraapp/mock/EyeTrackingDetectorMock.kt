package it.dani.cameraapp.mock

import android.annotation.SuppressLint
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import it.dani.cameraapp.camera.BoundingBox
import it.dani.cameraapp.camera.DetectedObject
import it.dani.cameraapp.camera.ObjectDetection

/**
 * @author Daniele
 *
 * This class emulates the behavior of [it.dani.cameraapp.camera.EyeTrackingDetector] (only for testing), simulates a sequence of eye movements
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
    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(image: ImageProxy) {

        if(System.currentTimeMillis() - this.oldTime > 5000) {
            this.oldTime = System.currentTimeMillis()

            val boundingBoxL = BoundingBox(
                POSITIONS[this@EyeTrackingDetectorMock.currentPosition].first,
                POSITIONS[this@EyeTrackingDetectorMock.currentPosition].second,
                POSITIONS[this@EyeTrackingDetectorMock.currentPosition].first + BOX_WIDTH,
                POSITIONS[this@EyeTrackingDetectorMock.currentPosition].second + BOX_HEIGHT
            )
            val detectedObjectL = DetectedObject(
                boundingBoxL,
                this.currentPosition, mutableListOf(com.google.mlkit.vision.objects.DetectedObject.Label("Eye L Mock",1f,1))
            )

            val boundingBoxR = BoundingBox(
                POSITIONS[this@EyeTrackingDetectorMock.currentPosition].first + BOX_WIDTH,
                POSITIONS[this@EyeTrackingDetectorMock.currentPosition].second,
                POSITIONS[this@EyeTrackingDetectorMock.currentPosition].first + BOX_WIDTH + BOX_WIDTH,
                POSITIONS[this@EyeTrackingDetectorMock.currentPosition].second + BOX_HEIGHT
            )
            val detectedObjectR = DetectedObject(
                boundingBoxR,
                this.currentPosition, mutableListOf(com.google.mlkit.vision.objects.DetectedObject.Label("Eye R Mock",1f,2))
            )

            if(this.currentPosition+1 < POSITIONS.size) {
                this.currentPosition++
            } else {
                this.currentPosition = 0
            }

            super.onSuccess.forEach { it(listOf(detectedObjectL,detectedObjectR)) }

            image.image?.let {
                val inputImage = InputImage.fromMediaImage(it,image.imageInfo.rotationDegrees)
                super.onGiveImageSize.forEach { it(inputImage.width,inputImage.height) }
            }
        }

        image.close()
    }

    companion object {
        /**
         * @property[BOX_WIDTH] The bounding box width
         */
        private const val BOX_WIDTH = 0.125f

        /**
         * @property[BOX_HEIGHT] The bounding box height
         */
        private const val BOX_HEIGHT = 0.125f

        /**
         * @property[POSITIONS] The list of fake movements
         */
        private val POSITIONS = listOf(0.0f to 0.0f, 1.0f - BOX_WIDTH*2 to 0.0f, 1.0f - BOX_WIDTH*2 to 1.0f - BOX_HEIGHT, 0.0f to 1.0f - BOX_HEIGHT)
    }
}