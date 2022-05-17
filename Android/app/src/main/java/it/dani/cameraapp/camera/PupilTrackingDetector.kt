package it.dani.cameraapp.camera

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.ImageProxy
import it.dani.cameraapp.camera.ImageUtils.rotateBitmap
import it.dani.cameraapp.camera.ImageUtils.toBitmap
import it.dani.cameraapp.ml.GazeMetadata
import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.label.Category
import org.tensorflow.lite.support.model.Model
import kotlin.collections.ArrayList

/**
 * @author Daniele
 *
 * This class extends [ObjectDetector], is dedicated to detect pupil on an image
 *
 * (n.b. this class is an another version of [PupilTrackingDetector])
 */

class PupilTrackingDetector(context: Context) : ObjectDetector() {

    /**
     * @property[objectDetector] The object detector object
     */
    private val objectDetector = try {
        val customObjectDetectorOptions = when(CompatibilityList().isDelegateSupportedOnThisDevice) {
            true -> Model.Options.Builder().setDevice(Model.Device.GPU).build()
            false -> {
                Log.i("GPU_delegate","Error: no GPU available on this device")
                Model.Options.Builder().setNumThreads(4).build()
            }
        }

        GazeMetadata.newInstance(context,customObjectDetectorOptions)
    } catch (e : IllegalArgumentException) {
        Log.e("GPU_delegate", "Error: GPU does not support this operations")

        val customObjectDetectorOptions = Model.Options.Builder().setNumThreads(4).build()
        GazeMetadata.newInstance(context, customObjectDetectorOptions)
    }


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

            val bitmap = img.toBitmap()
            val rotatedBitmap = bitmap.rotateBitmap(image.imageInfo.rotationDegrees.toFloat())

            val result = this.analyze(rotatedBitmap)
            Log.d("ANALYZER","(Pupil) Found: something [objects: ${result.size}]")

            this@PupilTrackingDetector.onGiveImageSize.forEach { it(width,height) }
            this@PupilTrackingDetector.onSuccess.forEach { it(rotatedBitmap,result.sortedByDescending { it.labels.averageScore()}) }

            img.close()
            image.close()
        }
    }

    override fun analyze(bitmap : Bitmap) : List<DetectedObject> {
        val width = minOf(bitmap.width,bitmap.height)
        val height = maxOf(bitmap.width,bitmap.height)

        val tensorImage = TensorImage.fromBitmap(bitmap)
        val outputs = this.objectDetector.process(tensorImage)

        val result : MutableList<DetectedObject> = ArrayList()
        outputs.detectionResultList.forEachIndexed { i,d ->
            if(d.scoreAsFloat >= this.accuracyThreshold) {
                val boundingBox = BoundingBox((d.locationAsRectF.left*1.0f)/width,
                    (d.locationAsRectF.top*1.0f)/height,
                    (d.locationAsRectF.right*1.0f)/width,
                    (d.locationAsRectF.bottom*1.0f)/height)

                result += DetectedObject(boundingBox,i, listOf(Category(d.categoryAsString,d.scoreAsFloat)))
            }
        }

        return result
    }
}