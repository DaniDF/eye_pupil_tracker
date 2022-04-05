package it.dani.cameraapp.view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import it.dani.cameraapp.R
import it.dani.cameraapp.view.utils.ViewUtils

/**
 * @author Daniele
 *
 * This class is the entry point for the app
 */

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.setContentView(R.layout.main_activity)
        ViewUtils.hideSystemBars(this.window)
    }

    override fun onResume() {
        super.onResume()
        findViewById<Button>(R.id.gameButton).apply {
            setOnClickListener {
                this.setOnClickListener {  }
            }
        }

        findViewById<Button>(R.id.calibrationButton).apply {
            setOnClickListener {
                val intent = Intent(this@MainActivity, CalibrationActivity::class.java)
                this@MainActivity.startActivity(intent)

                this.setOnClickListener {  }
            }
        }

        findViewById<Button>(R.id.nerdButton).apply {
            setOnClickListener {
                val intent = Intent(this@MainActivity, EyeTrackingActivity::class.java)
                this@MainActivity.startActivity(intent)

                this.setOnClickListener {  }
            }
        }
    }
}