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

import android.hardware.Camera
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.android.system.runtimepermissions.R
import kotlinx.android.synthetic.main.fragment_camera.*

/**
 * Displays a [CameraPreview] of the first [Camera].
 * An error message is displayed if the Camera is not available.
 *
 *
 * This Fragment is only used to illustrate that access to the Camera API has been granted (or
 * denied) as part of the runtime permissions model. It is not relevant for the use of the
 * permissions API.
 *
 *
 * Implementation is based directly on the documentation at
 * http://developer.android.com/guide/topics/media/camera.html
 */
class CameraPreviewFragment : Fragment() {

    private lateinit var preview: CameraPreview
    private var camera: Camera? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?): View {

        // Open an instance of the first camera and retrieve its info.
        camera = Camera.open(CAMERA_ID)
        val cameraInfo: Camera.CameraInfo = Camera.CameraInfo()

        if (camera != null) {
            // Get camera info only if the camera is available
            Camera.getCameraInfo(CAMERA_ID, cameraInfo)
        }

        val root: View

        if (camera == null) {
            // Camera is not available, display error message
            root = inflater.inflate(R.layout.fragment_camera_unavailable, container, false)
            Snackbar.make(root, "Camera is not available.", Snackbar.LENGTH_SHORT).show()
        } else {
            root = inflater.inflate(R.layout.fragment_camera, container, false)

            // Get the rotation of the screen to adjust the preview image accordingly.
            val displayRotation = activity.windowManager.defaultDisplay.rotation

            // Create the Preview view and set it as the content of this Activity.
            preview = CameraPreview(activity, camera, cameraInfo, displayRotation)
            cameraPreview.addView(preview)
        }

        return root
    }

    override fun onPause() {
        super.onPause()
        // Stop camera access
        releaseCamera()
    }

    private fun releaseCamera() {
        camera?.release() // release the camera for other applications.
        camera = null
    }

    companion object {

        /**
         * Id of the camera to access. 0 is the first camera.
         */
        private const val CAMERA_ID = 0

        fun newInstance() = CameraPreviewFragment()
    }
}
