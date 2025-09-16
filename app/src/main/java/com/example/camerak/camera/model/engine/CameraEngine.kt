package com.example.camerak.camera.model.engine

import android.view.Surface
import android.view.SurfaceHolder
import com.example.camerak.camera.viewmodel.CaptureMode

interface CameraEngine {
    suspend fun initializeCamera(previewHolder: SurfaceHolder)
    fun onPreviewSizeChanged(width: Int, height: Int)
    fun switchCamera()
    fun takePicture()
    fun recordVideo()
    fun changeMode(video: CaptureMode)
}