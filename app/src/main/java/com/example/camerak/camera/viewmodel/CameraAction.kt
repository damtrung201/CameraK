package com.example.camerak.camera.viewmodel

import android.view.SurfaceHolder

sealed interface CameraAction {
    data class PreviewSurfaceReady(val holder: SurfaceHolder) : CameraAction // <-- THAY ĐỔI
    data class PreviewSizeChanged(val width: Int, val height: Int) : CameraAction
    object ShutterClick : CameraAction
    object SwitchCamera : CameraAction

}