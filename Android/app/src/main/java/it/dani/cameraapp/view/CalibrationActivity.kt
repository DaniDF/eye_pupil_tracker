package it.dani.cameraapp.view

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LifecycleOwner
import it.dani.cameraapp.R
import it.dani.cameraapp.camera.CameraManager
import it.dani.cameraapp.camera.ObjectDetector
import it.dani.cameraapp.camera.PupilTrackingDetector
import it.dani.cameraapp.mock.EyeTrackingDetectorMock
import it.dani.cameraapp.motion.GazeMotionDetector
import it.dani.cameraapp.view.utils.PermissionUtils
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * @author Daniele
 *
 * Class for eye tracking calibration
 */

class CalibrationActivity : AppCompatActivity() {

    /**
     * @property[animationThread] The last launched animation thread
     */
    private lateinit var animationThread : ExecutorService

    /**
     * @property[dots] A list of displayed dots
     */
    private lateinit var dots : List<ImageButton>

    /**
     * @property[beenAskedPermission] Remember if is the first time asking for permissions
     */
    private var beenAskedPermission = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.setContentView(R.layout.calibration_activity)

        this.dots = listOf<ImageButton>(
            findViewById(R.id.calibration_dot_tl),
            findViewById(R.id.calibration_dot_tr),
            findViewById(R.id.calibration_dot_cc),
            findViewById(R.id.calibration_dot_bl),
            findViewById(R.id.calibration_dot_br)
        )
    }

    override fun onResume() {
        super.onResume()

        this.resetOpacity(this.dots)

        this.animationThread = this.animation(dots)

        if(!PermissionUtils.permissionGranted(this, arrayOf(Manifest.permission.CAMERA))) {
            if(!this.beenAskedPermission) {
                this.beenAskedPermission = true
                ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.CAMERA),0xA1)
            }
        } else {
            CameraManager.provideCamera(this,this::bindCameraCases)
        }
    }

    override fun onPause() {
        super.onPause()
        if(this::animationThread.isInitialized) {
            this.animationThread.shutdownNow()
        }
        CameraManager.undoCamera(this)
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
                    CameraManager.provideCamera(this,this::bindCameraCases)
                } else {
                    Toast.makeText(this,R.string.permission_camera_denied,Toast.LENGTH_SHORT).show()
                }
            }
        }
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

        val analyzer : ObjectDetector = EyeTrackingDetectorMock()

        this.animationThread = this.pulseAnimation(listOf(findViewById(R.id.calibration_dot_tl)))

        GazeMotionDetector(analyzer, PupilTrackingDetector(this)).apply {
            var gazeLeft : (Pair<Float,Float>,Pair<Float,Float>) -> Unit = {_,_->}
            var gazeRight : (Pair<Float,Float>,Pair<Float,Float>) -> Unit = {_,_->}
            var gazeUp : (Pair<Float,Float>,Pair<Float,Float>) -> Unit = {_,_->}
            var gazeDown : (Pair<Float,Float>,Pair<Float,Float>) -> Unit = {_,_->}

            gazeRight = { _, _ ->
                Log.d("Gaze","Locking right")
                this@CalibrationActivity.animationThread.shutdownNow()
                this@CalibrationActivity.animationThread = this@CalibrationActivity.pulseAnimation(listOf(findViewById(R.id.calibration_dot_tr)))
                onGazeRight -= gazeRight
            }
            gazeDown = { _, _ ->
                Log.d("Gaze","Locking down")
                this@CalibrationActivity.animationThread.shutdownNow()
                this@CalibrationActivity.animationThread = this@CalibrationActivity.pulseAnimation(listOf(findViewById(R.id.calibration_dot_br)))
                onGazeDown -= gazeDown
            }
            gazeLeft = { _, _ ->
                Log.d("Gaze","Locking left")
                this@CalibrationActivity.animationThread.shutdownNow()
                this@CalibrationActivity.animationThread = this@CalibrationActivity.pulseAnimation(listOf(findViewById(R.id.calibration_dot_bl)))
                onGazeLeft -= gazeLeft
            }
            gazeUp = { _, _ ->
                Log.d("Gaze","Locking up")
                this@CalibrationActivity.animationThread.shutdownNow()
                onGazeUp -= gazeUp
            }

            onGazeLeft += gazeLeft
            onGazeRight += gazeRight
            onGazeUp += gazeUp
            onGazeDown += gazeDown
        }

        analysis.setAnalyzer(Executors.newSingleThreadExecutor(),analyzer)

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
    private fun animation(objs : List<View>) : ExecutorService {
        return Executors.newCachedThreadPool().also {
            it.execute {
                runOnUiThread {
                    findViewById<TextView>(R.id.calibration_result_text).apply {
                        visibility = View.GONE
                    }
                }

                try {
                    val pulse = this.pulseAnimation(objs)
                    Thread.sleep(5000)
                    pulse.shutdownNow()

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
            }
        }
    }

    /**
     * This method performs a pulse animation with different values of opacity of provided views
     *
     * @param[objs] A list of views
     * @return The thread that actually is performing the animation
     */
    private fun pulseAnimation(objs : List<View>) : ExecutorService {
        return Executors.newCachedThreadPool().also {
            it.execute {
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
            }
        }
    }
}