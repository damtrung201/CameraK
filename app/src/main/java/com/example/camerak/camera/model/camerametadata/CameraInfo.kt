package com.example.camerak.camera.model.camerametadata
import android.hardware.camera2.CameraCharacteristics

data class CameraInfo(
    val id: String,
    val lensFacing: Int = CameraCharacteristics.LENS_FACING_BACK
)