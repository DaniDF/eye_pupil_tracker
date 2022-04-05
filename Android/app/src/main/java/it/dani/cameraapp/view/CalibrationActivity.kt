package it.dani.cameraapp.view

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.snackbar.Snackbar
import it.dani.cameraapp.R
import it.dani.cameraapp.camera.ObjectDetection
import it.dani.cameraapp.mock.EyeTrackingDetectorMock
import it.dani.cameraapp.motion.EyeMotionDetector
import it.dani.cameraapp.view.utils.PermissionUtils

/**
 * @author Daniele
 *
 * Class for eye tracking calibration
 */

class CalibrationActivity : AppCompatActivity() {

    /**
     * @property[animationThread] The last launched animation thread
     */
    private lateinit var animationThread : Thread

    /**
     * @property[dots] A list of displayed dots
     */
    private lateinit var dots : List<ImageButton>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.setContentView(R.layout.calibration_activity)

        this.dots = listOf<ImageButton>(findViewById(R.id.calibration_dot_tl),
            findViewById(R.id.calibration_dot_tr),
            findViewById(R.id.calibration_dot_cc),
            findViewById(R.id.calibration_dot_bl),
            findViewById(R.id.calibration_dot_br)
        )
    }

    override fun onResume() {
        super.onResume()

        this.resetOpacity(this.dots)

        //this.animationThread = this.animation(dots)

        if(!PermissionUtils.permissionGranted(this, arrayOf(Manifest.permission.CAMERA))) {
            ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.CAMERA),0xA1)
        } else {
            this.provideCamera()
        }
    }

    override fun onPause() {
        super.onPause()
        this.animationThread.interrupt()
        this.undoCamera()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode) {
            0xA1 -> {
                var flagOK = true
                for(countRes in grantResults.indices) {
                    if(grantResults[countRes] != PackageManager.PERMISSION_GRANTED) {
                        flagOK = false
                    }
                }

                if(flagOK) {
                    this.provideCamera()
                } else {
                    Snackbar.make(this.findViewById(R.id.main_view),
                        R.string.permission_camera_denied,
                        Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * This method attach listeners to camera
     */
    private fun provideCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            this.bindCameraCases(cameraProviderFuture.get())
        }, ContextCompat.getMainExecutor(this))
    }

    /**
     * This method remove all camera uses and close the camera
     */
    private fun undoCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.get().unbindAll()
        cameraProviderFuture.cancel(true)
    }

    /**
     * This method bind all the use cases to the camera
     *
     * @param[cameraProvider] The camera provider
     */
    private fun bindCameraCases(cameraProvider: ProcessCameraProvider) {

        val cameraSelected = CameraSelector.LENS_FACING_FRONT

        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(cameraSelected)
            .build()

        val analysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        val analyzer : ObjectDetection = EyeTrackingDetectorMock(this)

        this.animationThread = this.pulseAnimation(listOf(findViewById(R.id.calibration_dot_tl)))

        EyeMotionDetector(analyzer).apply {
            var eyeLeft : () -> Unit = {}
            var eyeRight : () -> Unit = {}
            var eyeUp : () -> Unit = {}
            var eyeDown : () -> Unit = {}

            eyeRight = {
                this@CalibrationActivity.animationThread.interrupt()
                this@CalibrationActivity.animationThread = this@CalibrationActivity.pulseAnimation(listOf(findViewById(R.id.calibration_dot_tr)))
                onEyeRight -= eyeRight
            }
            eyeDown = {
                this@CalibrationActivity.animationThread.interrupt()
                this@CalibrationActivity.animationThread = this@CalibrationActivity.pulseAnimation(listOf(findViewById(R.id.calibration_dot_br)))
                onEyeDown -= eyeDown
            }
            eyeLeft = {
                this@CalibrationActivity.animationThread.interrupt()
                this@CalibrationActivity.animationThread = this@CalibrationActivity.pulseAnimation(listOf(findViewById(R.id.calibration_dot_bl)))
                onEyeLeft -= eyeLeft
            }
            eyeUp = {
                this@CalibrationActivity.animationThread.interrupt()
                onEyeUp -= eyeUp
            }

            onEyeLeft += eyeLeft
            onEyeRight += eyeRight
            onEyeUp += eyeUp
            onEyeDown += eyeDown
        }

        analysis.setAnalyzer({ Thread(it).start() },analyzer)

        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(this as LifecycleOwner,cameraSelector,analysis)
    }

    /**
     * This method reset the opacity of provided views
     *
     * @param[objs] A list of views
     */
    private fun resetOpacity(objs : List<View>) {
        objs.forEach {
            it.background.alpha = (255 * 0.15).toInt()
        }
    }

    /**
     * This method performs a predefined animation with [dots]
     */
    private fun animation(objs : List<View>) : Thread {
        return Thread {

            runOnUiThread {
                findViewById<TextView>(R.id.calibration_result_text).apply {
                    visibility = View.GONE
                }
            }

            try {
                val pulse = this.pulseAnimation(objs)
                Thread.sleep(5000)
                pulse.interrupt()

                objs.forEach {
                    runOnUiThread {
                        it.background.alpha = (255 * 0.15).toInt()
                    }
                }

                /*
                objs.forEach {
                    val pulseO = this.pulseAnimation(listOf(it))
                    Thread.sleep(2500)
                    pulseO.interrupt()

                    runOnUiThread {
                        it.background.alpha = (255 * 0.15).toInt()
                    }
                }
                */

                runOnUiThread {
                    findViewById<TextView>(R.id.calibration_result_text).apply {
                        visibility = View.VISIBLE
                    }
                }

            } catch (e : InterruptedException) {}

        }.apply { start() }
    }

    /**
     * This method performs a pulse animation with different values of opacity of provided views
     *
     * @param[objs] A list of views
     * @return The thread that actually is performing the animation
     */
    private fun pulseAnimation(objs : List<View>) : Thread {
        return Thread {
            var value = 0
            var descending = false

            try {
                while(true) {
                    Thread.sleep(5)

                    objs.forEach {
                        runOnUiThread {
                            it.background.alpha = value
                        }
                    }

                    value = if(descending) {
                        if(value == 0) {
                            descending = false
                            value + 1
                        } else {
                            value - 1
                        }
                    } else {
                        if(value == 255) {
                            descending = true
                            value - 1
                        } else {
                            value + 1
                        }
                    }
                }
            } catch (e : InterruptedException) {
                runOnUiThread {
                    this.resetOpacity(objs)
                }
            }

        }.apply { start() }
    }
}