// viewmodel/CameraState.kt
package com.example.camerak.camera.viewmodel

import com.example.camerak.camera.model.camerametadata.CameraSettings


data class CameraState(
    val currentMode: CaptureMode = CaptureMode.PHOTO,
    val isRecording: Boolean = false,
    val settings: CameraSettings = CameraSettings()
)