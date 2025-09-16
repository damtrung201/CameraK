// viewmodel/CameraState.kt
package com.example.camerak.camera.viewmodel



data class CameraState(
    val currentMode: CaptureMode = CaptureMode.PHOTO,
    val previewState: PreviewState = PreviewState.IDLE,
    val cameraFacing: CameraFacing = CameraFacing.BACK
)