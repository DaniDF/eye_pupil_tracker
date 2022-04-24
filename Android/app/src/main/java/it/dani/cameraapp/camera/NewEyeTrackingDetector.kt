package it.dani.cameraapp.camera

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.camera.core.ImageProxy
import it.dani.cameraapp.camera.ImageUtils.toBitmap
import it.dani.cameraapp.ml.ConvertedModelDflMetadata
import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.label.Category
import org.tensorflow.lite.support.model.Model
import kotlin.collections.ArrayList

/**
 * @author Daniele
 *
 * This class extends [ObjectDetection], is dedicated to detect eye on an image
 *
 * (n.b. this class is an another version of [EyeTrackingDetector])
 */

class NewEyeTrackingDetector(context: Context) : ObjectDetection() {

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

        ConvertedModelDflMetadata.newInstance(context,customObjectDetectorOptions)
    } catch (e : IllegalArgumentException) {
        Log.e("GPU_delegate", "Error: GPU does not support this operations")

        val customObjectDetectorOptions = Model.Options.Builder().setNumThreads(4).build()
        ConvertedModelDflMetadata.newInstance(context, customObjectDetectorOptions)
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

            val tensorImage = TensorImage.fromBitmap(img.toBitmap())

            val outputs = this.objectDetector.process(tensorImage)

            Log.d("ANALYZER","Found: something [objects: ${outputs.detectionResultList.size}]")

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

            this@NewEyeTrackingDetector.onGiveImageSize.forEach { it(width,height) }
            this@NewEyeTrackingDetector.onSuccess.forEach { it(result) }

            img.close()
            image.close()
        }
    }
}