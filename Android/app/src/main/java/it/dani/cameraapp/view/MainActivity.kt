package it.dani.cameraapp.view

import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import it.dani.cameraapp.R
import it.dani.cameraapp.view.sensor.GyroscopeListener
import it.dani.cameraapp.view.utils.ViewUtils

/**
 * @author Daniele
 *
 * This class is the entry point for the app
 */

class MainActivity : AppCompatActivity() {

    private lateinit var sensorManager : SensorManager
    private var accelerometerSensor : Sensor? = null
    private lateinit var gyroscopeListener : GyroscopeListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.setContentView(R.layout.main_activity)
        ViewUtils.hideSystemBars(this.window)

        this.sensorManager = this.getSystemService(SENSOR_SERVICE) as SensorManager
        this.accelerometerSensor = this.sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        if(this.accelerometerSensor == null) {
            Log.e("Sensors","Error: no accelerometer found")
        }

        this.gyroscopeListener = GyroscopeListener.get().apply {
            onChange += {
                val calcX = it.values[0].map(10f,-10f,0.25f,0.75f)
                val calcY = it.values[1].map(-10f,10f,0.25f,0.75f)
                findViewById<ImageView>(R.id.imageTarget).apply {
                    val params = layoutParams as ConstraintLayout.LayoutParams
                    params.horizontalBias = calcX
                    params.verticalBias = calcY
                    layoutParams = params
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        findViewById<Button>(R.id.gameButton).apply {
            setOnClickListener {
                val intent = Intent(this@MainActivity,GameActivity::class.java)
                this@MainActivity.startActivity(intent)

                this.setOnClickListener {  }
            }
        }

        findViewById<ImageButton>(R.id.calibrationButton).apply {
            setOnClickListener {
                val intent = Intent(this@MainActivity, CalibrationActivity::class.java)
                this@MainActivity.startActivity(intent)

                this.setOnClickListener {  }
            }
        }

        findViewById<ImageButton>(R.id.settingsButton).apply {
            setOnClickListener {
                val intent = Intent(this@MainActivity, EyeTrackingActivity::class.java)
                this@MainActivity.startActivity(intent)

                this.setOnClickListener {  }
            }
        }

        this.accelerometerSensor?.also { sensor ->
            this.sensorManager.registerListener(gyroscopeListener,sensor,SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        this.sensorManager.unregisterListener(this.gyroscopeListener)
    }

    private fun Float.map(inStart : Float, inEnd : Float, outStart : Float, outEnd : Float) : Float {
        return outStart + ((outEnd - outStart) / (inEnd - inStart)) * (this - inStart)
    }
}