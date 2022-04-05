package it.dani.cameraapp.view.utils

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

/**
 * @author Daniele
 *
 * An app permission utility class
 */
object PermissionUtils {

    /**
     * This method checks if for a provided [context] a list of [permissions] are granted
     *
     * @param[context] The context to check
     * @param[permissions] A list of permissions to be checked
     * @return true if all the permissions in [permissions] are granted, false otherwise
     */
    fun permissionGranted(context: Context, permissions: Array<out String>) : Boolean {
        var result = true

        for(element in permissions) {
            result = (ContextCompat.checkSelfPermission(context, element) == PackageManager.PERMISSION_GRANTED)
        }

        return result
    }
}