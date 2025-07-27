package com.example.camerak.camera.model.engine

import android.view.Surface
import android.view.SurfaceHolder

interface CameraEngine {
    suspend fun initializeCamera(previewHolder: SurfaceHolder)
    fun onPreviewSizeChanged(width: Int, height: Int)
}