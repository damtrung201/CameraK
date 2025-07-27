// model/engine/core/CaptureRequestBuilder.kt
package com.yourcompany.camerapp.camera.model.engine.core

import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CaptureRequest
import android.view.Surface
import com.example.camerak.camera.model.camerametadata.FlashMode

class CaptureRequestBuilder(private val device: CameraDevice) {
    fun buildPreviewRequest(previewSurface: Surface): CaptureRequest {
        val builder = device.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW).apply {
            addTarget(previewSurface)
            set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)
        }
        return builder.build()
    }
    fun buildStillCaptureRequest(imageReaderSurface: Surface, flashMode: FlashMode): CaptureRequest {
        val builder = device.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE).apply {
            addTarget(imageReaderSurface)
            // TODO: Set flash mode based on input
        }
        return builder.build()
    }
}