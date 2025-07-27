// model/engine/core/CameraSessionManager.kt
package com.example.camerak.camera.model.engine.core

import android.hardware.camera2.*
import android.os.Handler
import android.os.HandlerThread
import android.view.Surface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class CameraSessionManager {
    private var captureSession: CameraCaptureSession? = null
    val isSessionReady = MutableStateFlow(false)
    private val cameraThread = HandlerThread("SessionThread").apply { start() }
    private val cameraHandler = Handler(cameraThread.looper)

    suspend fun createSession(device: CameraDevice, targets: List<Surface>) = suspendCancellableCoroutine { continuation ->
        device.createCaptureSession(targets, object : CameraCaptureSession.StateCallback() {
            override fun onConfigured(session: CameraCaptureSession) {
                captureSession = session
                isSessionReady.value = true
                if (continuation.isActive) continuation.resume(Unit)
            }
            override fun onConfigureFailed(session: CameraCaptureSession) {
                isSessionReady.value = false
                if (continuation.isActive) continuation.resumeWithException(RuntimeException("Session configuration failed"))
            }
        }, cameraHandler)
    }

    fun setRepeatingRequest(request: CaptureRequest) {
        captureSession?.setRepeatingRequest(request, null, cameraHandler)
    }

    fun capture(request: CaptureRequest) {
        captureSession?.capture(request, null, cameraHandler)
    }

    fun close() {
        captureSession?.close()
        captureSession = null
        isSessionReady.value = false
        cameraThread.quitSafely()
    }
}