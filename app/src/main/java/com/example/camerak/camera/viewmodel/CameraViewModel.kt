package com.example.camerak.camera.viewmodel

import android.util.Log
import android.view.SurfaceHolder
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.camerak.camera.model.camerametadata.AspectRatioValue
import com.example.camerak.camera.model.camerametadata.CameraSettings
import com.example.camerak.camera.model.camerametadata.FilterValue
import com.example.camerak.camera.model.camerametadata.FlashValue
import com.example.camerak.camera.model.camerametadata.SettingKey
import com.example.camerak.camera.model.camerametadata.TimerValue
import com.example.camerak.camera.model.engine.CameraEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CameraViewModel(
    private val cameraEngine: CameraEngine,
    private val engineScope: CoroutineScope,
    private val cameraSettings: CameraSettings
) : ViewModel() {

    private val _cameraState = MutableStateFlow(CameraState())
    val cameraState = _cameraState.asStateFlow()

    private val _quickSettingsState = MutableStateFlow(
        // Khởi tạo trạng thái ban đầu với các item hoàn chỉnh
        QuickSettingsUiState(
            settingItems = listOf(
                QuickSettingItemState(
                    key = SettingKey.Flash,
                    currentValue = FlashValue.OFF,
                    availableValues = listOf(FlashValue.OFF, FlashValue.ON, FlashValue.AUTO)
                ),
                QuickSettingItemState(
                    key = SettingKey.AspectRatio,
                    currentValue = AspectRatioValue.RATIO_4_3,
                    availableValues = listOf(AspectRatioValue.RATIO_4_3, AspectRatioValue.RATIO_16_9)
                ),
                QuickSettingItemState(
                    key = SettingKey.Timer,
                    currentValue = TimerValue.OFF,
                    availableValues = listOf(TimerValue.OFF, TimerValue.TIMER_3S, TimerValue.TIMER_5S, TimerValue.TIMER_10S)
                ),
                QuickSettingItemState(
                    key = SettingKey.Filter,
                    currentValue = FilterValue.OFF,
                    availableValues = listOf(FilterValue.OFF, FilterValue.FILTER_1, FilterValue.FILTER_2, FilterValue.FILTER_3)
                )
            )
        )
    )
    val quickSettingsState = _quickSettingsState.asStateFlow()

    private val actionMap: Map<CameraAction, () -> Unit> = mapOf(
        CameraAction.ShutterClick to ::handleShutterClick,
        CameraAction.SwitchCamera to ::handleSwitchCamera
    )

    private val captureActionMap: Map<CaptureMode, () -> Unit> = mapOf(
        CaptureMode.PHOTO to ::handlePhotoCapture,
        CaptureMode.VIDEO to ::handleVideoCapture
    )
    private fun handleChangeMode(){
        Log.i("trung.dam", "handleChangeMode: ")
        cameraEngine.changeMode(CaptureMode.VIDEO)
        _cameraState.value = _cameraState.value.copy(currentMode = CaptureMode.VIDEO)
    }
    private fun handleSwitchCamera() {
        Log.i("trung.dam", "handleSwitchCamera: ")
        cameraEngine.switchCamera()
    }
    private fun handleShutterClick() {
        captureActionMap.get(cameraState.value.currentMode)?.invoke()
    }
    private fun handlePhotoCapture() {
        Log.i("trung.dam", "handlePhotoCapture: ")
        cameraEngine.takePicture()
    }

    private fun handleVideoCapture() {
        Log.i("trung.dam", "handleVideoCapture: ")
        cameraEngine.recordVideo()
    }
    fun onSettingClicked(key: SettingKey) {
//        _quickSettingsState.update { currentState ->
//            val newItems = currentState.settingItems.map { item ->
//                if (item.key == key) {
//                    // Tìm giá trị tiếp theo trong danh sách availableValues
//                    val currentIndex = item.availableValues.indexOf(item.currentValue)
//                    val nextIndex = (currentIndex + 1) % item.availableValues.size
//                    val nextValue = item.availableValues[nextIndex]
//
//                    // Áp dụng thay đổi vào Repository
////                    repository.applySetting(key, nextValue)
//
//                    // Trả về item mới với giá trị đã được cập nhật
//                    item.copy(currentValue = nextValue)
//                } else {
//                    item // Giữ nguyên các item khác
//                }
//            }
//            currentState.copy(settingItems = newItems)
//        }
    }
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
                    handleSwitchCamera()
                }
                CameraAction.ShutterClick -> {
                    handleShutterClick()
                }

                CameraAction.ChangeMode -> {
                    handleChangeMode()
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

