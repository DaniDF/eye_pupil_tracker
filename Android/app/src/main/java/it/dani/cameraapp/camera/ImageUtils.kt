package it.dani.cameraapp.camera

import android.graphics.*
import android.media.Image
import java.io.ByteArrayOutputStream

/**
 * @author Daniele
 *
 * This class provides utilities for operating on [Image] classes
 */
object ImageUtils {

    /**
     * This method transform an [Image] into a [Bitmap]
     *
     * @return The converted bitmap
     */
    fun Image.toBitmap(): Bitmap {
        val yBuffer = planes[0].buffer
        val uBuffer = planes[1].buffer
        val vBuffer = planes[2].buffer

        val ySize: Int = yBuffer.remaining()
        val uSize: Int = uBuffer.remaining()
        val vSize: Int = vBuffer.remaining()

        val nv21 = ByteArray(ySize + uSize + vSize)
        //U and V are swapped
        //U and V are swapped
        yBuffer.get(nv21, 0, ySize)
        vBuffer.get(nv21, ySize, vSize)
        uBuffer.get(nv21, ySize + vSize, uSize)

        val yuvImage = YuvImage(nv21, ImageFormat.NV21, this.width, this.height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 75, out)

        val imageBytes = out.toByteArray()
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }

    /**
     * This method rotates the bitmap of a given degree
     *
     * @param[angle] Degree of rotation (0 - 360)
     */
    fun Bitmap.rotateBitmap(angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(
            this, 0, 0, this.width, this.height, matrix, true
        )
    }
}