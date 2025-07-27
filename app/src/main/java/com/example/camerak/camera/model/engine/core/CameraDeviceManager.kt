// model/engine/core/CameraDeviceManager.kt
package com.example.camerak.camera.model.engine.core

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.camera2.*
import android.os.Handler
import android.os.HandlerThread
import com.example.camerak.camera.model.camerametadata.CameraInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class CameraDeviceManager(
    context: Context,
    private val cameraManager: CameraManager
) {
    private val cameraThread = HandlerThread("CameraThread").apply { start() }
    private val cameraHandler = Handler(cameraThread.looper)
    private val _cameraDeviceFlow = MutableStateFlow<CameraDevice?>(null)
    val cameraDeviceFlow: StateFlow<CameraDevice?> = _cameraDeviceFlow

    fun getAvailableCameras(): List<CameraInfo> {
        return cameraManager.cameraIdList.map { id ->
            val characteristics = cameraManager.getCameraCharacteristics(id)
            val facing = characteristics.get(CameraCharacteristics.LENS_FACING) ?: CameraCharacteristics.LENS_FACING_BACK
            CameraInfo(id, facing)
        }
    }

    @SuppressLint("MissingPermission")
    suspend fun openCamera(cameraId: String) = suspendCancellableCoroutine { continuation ->
        try {
            cameraManager.openCamera(cameraId, object : CameraDevice.StateCallback() {
                override fun onOpened(device: CameraDevice) {
                    _cameraDeviceFlow.value = device
                    if (continuation.isActive) continuation.resume(Unit)
                }
                override fun onDisconnected(device: CameraDevice) {
                    device.close()
                    _cameraDeviceFlow.value = null
                }
                override fun onError(device: CameraDevice, error: Int) {
                    device.close()
                    _cameraDeviceFlow.value = null
                    if (continuation.isActive) continuation.resumeWithException(RuntimeException("Camera error: $error"))
                }
            }, cameraHandler)
        } catch (e: Exception) {
            if (continuation.isActive) continuation.resumeWithException(e)
        }
    }

    fun close() {
        _cameraDeviceFlow.value?.close()
        _cameraDeviceFlow.value = null
        cameraThread.quitSafely()
    }
}