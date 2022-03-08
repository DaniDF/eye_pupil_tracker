package it.dani.cameraapp

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

object PermissionUtils {
    fun permissionGranted(context: Context, permissions: Array<out String>) : Boolean {
        var result = true

        for(element in permissions) {
            result = (ContextCompat.checkSelfPermission(context, element) == PackageManager.PERMISSION_GRANTED)
        }

        return result
    }
}