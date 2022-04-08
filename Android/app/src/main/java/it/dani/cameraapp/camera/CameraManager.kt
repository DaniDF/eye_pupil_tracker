package it.dani.cameraapp.camera

import android.content.Context
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat

object CameraManager {

    /**
     * This method attach listeners to camera
     *
     * @param[context] That are working on
     * @param[bindCameraCases] The bind use cases handler
     */
    fun provideCamera(context: Context, bindCameraCases : (ProcessCameraProvider) -> Any) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            bindCameraCases(cameraProviderFuture.get())
        }, ContextCompat.getMainExecutor(context))
    }

    /**
     * This method remove all camera uses and close the camera
     *
     * @param[context] That are working on
     */
    fun undoCamera(context : Context) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.get().unbindAll()
        cameraProviderFuture.cancel(true)
    }
}