/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.system.runtimepermissions.extensions

import android.hardware.Camera
import android.view.Surface

/**
 * Calculate the correct orientation for a [Camera] preview that is displayed on screen.
 *
 *
 * Implementation is based on the sample code provided in
 * [Camera.setDisplayOrientation].
 */
fun Camera.CameraInfo.calculatePreviewOrientation(rotation: Int): Int {

    val degrees = when (rotation) {
        Surface.ROTATION_0 -> 0
        Surface.ROTATION_90 -> 90
        Surface.ROTATION_180 -> 180
        Surface.ROTATION_270 -> 270
        else -> 0
    }

    return when (facing) {
        Camera.CameraInfo.CAMERA_FACING_FRONT -> (360 - ((orientation + degrees) % 360)) % 360
        Camera.CameraInfo.CAMERA_FACING_BACK -> (orientation - degrees + 360) % 360
        else -> orientation
    }
}