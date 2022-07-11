package it.dani.cameraapp.motion

import android.graphics.Bitmap
import android.util.Log
import it.dani.cameraapp.camera.DetectedObject
import it.dani.cameraapp.camera.ObjectDetector
import java.util.concurrent.Executors
import java.util.concurrent.Semaphore

/**
 * @author Daniele
 *
 * This class wrap an [ObjectDetector] used for detect the gaze movements
 *
 * @param[eyeObjectDetection] An object detection that finds eyes in an image
 * @param[pupilObjectDetection] An object detection that finds pupil in an image
 */
class GazeMotionDetector(eyeObjectDetection: ObjectDetector,pupilObjectDetection: ObjectDetector) {

    /**
     * @property[leftLimit] //TODO
     */
    var leftLimit = 0.35f

    /**
     * @property[topLimit] //TODO
     */
    var topLimit = 0.35f

    /**
     * @property[rightLimit] //TODO
     */
    var rightLimit = 0.65f

    /**
     * @property[bottomLimit] //TODO
     */
    var bottomLimit = 0.65f

    /**
     * @property[onGazeLeft] List of handler fired when gaze move to left
     */
    val onGazeLeft : MutableList<(Pair<Float,Float>,Pair<Float,Float>) -> Any> = ArrayList()

    /**
     * @property[onGazeRight] List of handler fired when gaze move to right
     */
    val onGazeRight : MutableList<(Pair<Float,Float>,Pair<Float,Float>) -> Any> = ArrayList()

    /**
     * @property[onGazeUp] List of handler fired when gaze move to up
     */
    val onGazeUp : MutableList<(Pair<Float,Float>,Pair<Float,Float>) -> Any> = ArrayList()

    /**
     * @property[onGazeDown] List of handler fired when gaze move to down
     */
    val onGazeDown : MutableList<(Pair<Float,Float>,Pair<Float,Float>) -> Any> = ArrayList()

    private val analyzeSemaphore = Semaphore(3)

    init {
        val sortPupils : (Pair<Pair<Float,Float>,Pair<Float,Float>>) -> Pair<Pair<Float,Float>,Pair<Float,Float>> = {
            if(it.first.first < it.second.first) {
                it
            } else if (it.first.first > it.second.first) {
                it.second to it.first
            } else {
                if(it.first.second > it.second.second) {
                    (it.second) to (it.first)
                } else {
                    it
                }
            }
        }

        eyeObjectDetection.onSuccess += { bitmap, eyes ->
            if(eyes.size >= 2 && this.analyzeSemaphore.tryAcquire()) {

                Executors.newSingleThreadExecutor().also {
                    it.execute {
                        try {
                            //val (firstPupil,secondPupil) = sortPupils(this.findPupil(bitmap,eyes[0],pupilObjectDetection) to this.findPupil(bitmap,eyes[1],pupilObjectDetection))

                            val firstPupil = this.findPupil(bitmap,eyes[0],pupilObjectDetection)
                            val secondPupil = firstPupil

                            if(firstPupil.first <= this.leftLimit || secondPupil.first <= this.leftLimit) {
                                this.onGazeLeft.forEach { it(firstPupil,secondPupil) }
                            } else if(firstPupil.first >= this.rightLimit || secondPupil.first >= this.rightLimit) {
                                this.onGazeRight.forEach { it(firstPupil,secondPupil) }
                            }

                            if(firstPupil.second <= this.topLimit || secondPupil.second <= this.topLimit) {
                                this.onGazeUp.forEach { it(firstPupil,secondPupil) }
                            } else if(firstPupil.second >= this.bottomLimit || secondPupil.second >= this.bottomLimit) {
                                this.onGazeDown.forEach { it(firstPupil,secondPupil) }
                            }

                        } catch (e : IllegalArgumentException) {}
                        catch (e : IllegalDetectionException) {
                            Log.e("Pupil","No pupils in the bitmap: ${e.message}")
                        }

                        this.analyzeSemaphore.release()
                    }
                }
            }
        }
    }

    /**
     * This method from a [Bitmap] and a detected eye finds the center of a pupil
     *
     * @param[bitmap] An image in [Bitmap]
     * @param[eyeDetectedObject] An [DetectedObject] representing the position of an eye
     * @param[objectDetector] An [ObjectDetector] that finds a pupil in the eye
     * @return The center on the pupil
     * @throws[IllegalDetectionException] If there no pupil in the bitmap
     */
    private fun findPupil(bitmap: Bitmap, eyeDetectedObject: DetectedObject, objectDetector: ObjectDetector) : Pair<Float,Float> {
        val eyeBox = eyeDetectedObject.boundingBox
        val x = (eyeBox.left * bitmap.width).toInt()
        val y = (eyeBox.top * bitmap.height).toInt()
        val bitmapWidth = ((eyeBox.right - eyeBox.left) * bitmap.width).toInt()
        val bitmapHeight = ((eyeBox.bottom - eyeBox.top) * bitmap.height).toInt()
        val croppedBitmap = Bitmap.createBitmap(bitmap,x,y,bitmapWidth,bitmapHeight)
        val pupils = objectDetector.analyze(croppedBitmap)

        if(pupils.isEmpty()) throw IllegalDetectionException("No pupil found in bitmap")

        return this.pupilPosition(pupils.first())
    }

    /**
     * This method returns the center of a detected pupil from a given [DetectedObject]
     *
     * @param[detectedObject] The detected pupil
     * @return The [Pair] x,y of the calculated point
     */
    private fun pupilPosition(detectedObject : DetectedObject) : Pair<Float,Float> {
        return (detectedObject.boundingBox.right + detectedObject.boundingBox.left) / 2 to
                (detectedObject.boundingBox.bottom + detectedObject.boundingBox.top) / 2
    }

}