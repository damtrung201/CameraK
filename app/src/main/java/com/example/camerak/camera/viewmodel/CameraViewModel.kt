package com.example.camerak.camera.viewmodel

import android.view.SurfaceHolder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.camerak.camera.model.engine.CameraEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CameraViewModel(
    private val cameraEngine: CameraEngine,
    private val engineScope: CoroutineScope
) : ViewModel() {

    private val _cameraState = MutableStateFlow(CameraState())
    val cameraState = _cameraState.asStateFlow()

    /**
     * Hàm chính để nhận và xử lý tất cả các hành động từ View.
     */
    fun onAction(action: CameraAction) {
        viewModelScope.launch {
            when (action) {
                is CameraAction.PreviewSurfaceReady -> {
                    cameraEngine.initializeCamera(action.holder)
                }
                is CameraAction.PreviewSizeChanged -> {
                    cameraEngine.onPreviewSizeChanged(action.width, action.height)
                }
                CameraAction.SwitchCamera -> {
                    // TODO: Logic chuyển đổi camera
                }
                CameraAction.TakePhoto -> {
                    // TODO: Logic chụp ảnh
                }
                CameraAction.ToggleRecording -> {
                    // TODO: Logic bắt đầu/dừng quay video
                }
            }
        }
    }

    /**
     * Cập nhật chế độ chụp (PHOTO/VIDEO).
     */
    fun setCaptureMode(mode: CaptureMode) {
        _cameraState.update { it.copy(currentMode = mode) }
        // TODO: Yêu cầu CameraEngine thay đổi chế độ hoạt động
        // Ví dụ: cameraEngine.setCaptureMode(mode)
    }

    /**
     * Được gọi khi ViewModel bị hủy.
     * Dọn dẹp tài nguyên để tránh rò rỉ bộ nhớ.
     */
    override fun onCleared() {
        super.onCleared()
        // Hủy tất cả các coroutine đang chạy trong scope của engine
        engineScope.cancel()
        // TODO: Gọi hàm giải phóng camera trong engine
        // Ví dụ: cameraEngine.release()
    }
}