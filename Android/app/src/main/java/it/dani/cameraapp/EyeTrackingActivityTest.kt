package it.dani.cameraapp


import android.graphics.*
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import it.dani.cameraapp.camera.EyeTrackingDetector
import it.dani.cameraapp.camera.ObjectDetector
import it.dani.cameraapp.camera.PupilTrackingDetector

class EyeTrackingActivityTest : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.eyeTrackingTest()

    }

    private fun eyeTrackingTest() {
        val eyeTrackingDetector : ObjectDetector = EyeTrackingDetector(this)
        val pupilTrackingDetector : ObjectDetector = PupilTrackingDetector(this)

        val bitmap = BitmapFactory.decodeResource(this.resources,R.drawable.human_face)

        val eyes = eyeTrackingDetector.analyze(bitmap)

        val cpyBitmap = bitmap.copy(Bitmap.Config.ARGB_8888,true)

        eyes.subList(0,2).forEach { eye ->
            try {
                val eyeBox = eye.boundingBox
                val x = (eyeBox.left * bitmap.width).toInt()
                val y = (eyeBox.top * bitmap.height).toInt()
                val bitmapWidth = ((eyeBox.right - eyeBox.left) * bitmap.width).toInt()
                val bitmapHeight = ((eyeBox.bottom - eyeBox.top) * bitmap.height).toInt()
                val croppedBitmap = Bitmap.createBitmap(bitmap,x,y,bitmapWidth,bitmapHeight)

                val pupils = pupilTrackingDetector.analyze(croppedBitmap)

                pupils.subList(0,2).forEach { pupil ->
                    val pupilBox = pupil.boundingBox
                    val cpyCroppedBitmap = croppedBitmap.copy(Bitmap.Config.ARGB_8888,true)
                    val canvas = Canvas(cpyCroppedBitmap)

                    val rectF = Rect((pupilBox.left.coerceAtMost(1.0f) * canvas.width).toInt(),
                        (pupilBox.top.coerceAtMost(1.0f) * canvas.height).toInt(),
                        (pupilBox.right.coerceAtMost(1.0f) * canvas.width).toInt(),
                        (pupilBox.bottom.coerceAtMost(1.0f) * canvas.height).toInt())

                    canvas.drawRect(rectF, Paint().apply {
                        color = Color.RED
                        strokeWidth = 10f
                        style = Paint.Style.STROKE
                    })

                    val canvasTotal = Canvas(cpyBitmap)

                    val rectFTotal = Rect(
                        ((eyeBox.left + (eyeBox.right - eyeBox.left) * pupilBox.left) * canvasTotal.width).toInt(),
                        ((eyeBox.top + (eyeBox.bottom - eyeBox.top) * pupilBox.top) * canvasTotal.height).toInt(),
                        ((eyeBox.left + (eyeBox.right - eyeBox.left) * pupilBox.right) * canvasTotal.width).toInt(),
                        ((eyeBox.top + (eyeBox.bottom - eyeBox.top) * pupilBox.bottom) * canvasTotal.height).toInt())

                    canvasTotal.drawRect(rectFTotal, Paint().apply {
                        color = Color.RED
                        strokeWidth = 10f
                        style = Paint.Style.STROKE
                    })
                }
            } catch (e : IllegalArgumentException) {}
        }

        Log.d("canvas","canvas")
    }
}