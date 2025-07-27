package com.yourcompany.camerapp.camera.model.engine

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.util.Size
import android.view.SurfaceHolder
import com.example.camerak.camera.model.engine.CameraEngine
import com.example.camerak.camera.model.engine.core.CameraDeviceManager
import com.example.camerak.camera.model.engine.core.CameraSessionManager
import com.yourcompany.camerapp.camera.model.engine.core.CaptureRequestBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import kotlin.math.abs

@SuppressLint("MissingPermission")
class Camera2EngineImpl(
    private val context: Context,
    private val coroutineScope: CoroutineScope
) : CameraEngine {

    private val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    private val deviceManager = CameraDeviceManager(context, cameraManager)
    private val sessionManager = CameraSessionManager()
    private var requestBuilder: CaptureRequestBuilder? = null

    // THAY ĐỔI: Nhận SurfaceHolder thay vì Surface
    override suspend fun initializeCamera(previewHolder: SurfaceHolder) {
        val backCameraId = deviceManager.getAvailableCameras().firstOrNull {
            it.lensFacing == CameraCharacteristics.LENS_FACING_BACK
        }?.id ?: "0"


        val characteristics = cameraManager.getCameraCharacteristics(backCameraId)
        val configMap = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)!!

        // Logic mới để chọn kích thước và set cho surface
        val previewSize = chooseOptimalPreviewSize(configMap.getOutputSizes(SurfaceHolder::class.java))
        previewHolder.setFixedSize(previewSize.width, previewSize.height)

        val previewSurface = previewHolder.surface

        coroutineScope.launch {
            deviceManager.openCamera(backCameraId)
            deviceManager.cameraDeviceFlow.filterNotNull().collectLatest { device ->
                requestBuilder = CaptureRequestBuilder(device)
                sessionManager.createSession(device, listOf(previewSurface))
                sessionManager.isSessionReady.filter { it }.collectLatest { isReady ->
                    if (isReady) {
                        val previewRequest = requestBuilder!!.buildPreviewRequest(previewSurface)
                        sessionManager.setRepeatingRequest(previewRequest)
                    }
                }
            }
        }
    }

    // Hàm mới để chọn kích thước
    private fun chooseOptimalPreviewSize(supportedSizes: Array<Size>): Size {
        val targetRatio = 4.0 / 3.0
        return supportedSizes.filter {
            abs((it.width.toDouble() / it.height) - targetRatio) < 0.1
        }.maxByOrNull { it.height * it.width } ?: supportedSizes[0]
    }

    override fun onPreviewSizeChanged(width: Int, height: Int) {
        // TODO: Handle preview size changes
    }
}