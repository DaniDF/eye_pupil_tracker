package it.dani.cameraapp.view.sensor

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener

class GyroscopeListener private constructor(): SensorEventListener {
    val onChange : MutableList<(SensorEvent) -> Any> = ArrayList()

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let { e -> this.onChange.forEach { it(e) } }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    companion object {
        private val instance = GyroscopeListener()

        fun get() : GyroscopeListener {
            return instance
        }
    }
}