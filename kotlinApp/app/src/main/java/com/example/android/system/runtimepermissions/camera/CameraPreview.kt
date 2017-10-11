/*
* Copyright 2017 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.example.android.system.runtimepermissions.camera

import android.content.Context
import android.hardware.Camera
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.example.android.system.runtimepermissions.extensions.calculatePreviewOrientation
import java.io.IOException

/**
 * Camera preview that displays a [Camera].
 *
 *
 * Handles basic lifecycle methods to display and stop the preview.
 *
 *
 * Implementation is based directly on the documentation at
 * http://developer.android.com/guide/topics/media/camera.html
 *
 *
 * Using deprecated android.hardware.Camera API in order to support {14 < API < 21}.
 */
class CameraPreview @JvmOverloads constructor(
        context: Context,
        private val camera: Camera? = null,
        private val cameraInfo: Camera.CameraInfo? = null,
        private val displayOrientation: Int = 0
) : SurfaceView(context), SurfaceHolder.Callback {

    private var surfaceHolder: SurfaceHolder? = null

    init {

        // Do not initialise if no camera has been set
        if (camera != null && cameraInfo != null) {
            // Install a SurfaceHolder.Callback so we get notified when the
            // underlying surface is created and destroyed.
            surfaceHolder = holder.apply {
                addCallback(this@CameraPreview)
            }
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            camera?.run {
                setPreviewDisplay(holder)
                startPreview()
                Log.d(TAG, "Camera preview started.")
            }
        } catch (e: IOException) {
            Log.d(TAG, "Error setting camera preview: " + e.message)
        }

    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        // Empty. Take care of releasing the Camera preview in your activity.
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, w: Int, h: Int) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (surfaceHolder?.surface == null) {
            // preview surface does not exist
            Log.d(TAG, "Preview surface does not exist")
            return
        }

        // stop preview before making changes
        try {
            camera?.run {
                stopPreview()
                Log.d(TAG, "Preview stopped.")
            }
        } catch (e: Exception) {
            // ignore: tried to stop a non-existent preview
            Log.d(TAG, "Error starting camera preview: " + e.message)
        }

        try {
            camera?.run {
                cameraInfo?.run {
                    setDisplayOrientation(calculatePreviewOrientation(displayOrientation))
                }
                setPreviewDisplay(surfaceHolder)
                startPreview()
                Log.d(TAG, "Camera preview started.")
            }
        } catch (e: Exception) {
            Log.d(TAG, "Error starting camera preview: " + e.message)
        }
    }

    companion object {
        private const val TAG = "CameraPreview"
    }
}