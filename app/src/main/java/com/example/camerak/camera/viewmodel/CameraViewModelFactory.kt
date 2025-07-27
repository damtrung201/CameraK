package com.example.camerak.camera.viewmodel



import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.camerak.camera.model.engine.CameraEngine
import com.yourcompany.camerapp.camera.model.engine.Camera2EngineImpl
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

class CameraViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CameraViewModel::class.java)) {
            // Tạo một scope riêng để truyền vào Engine
            val engineScope = MainScope()

            val engine: CameraEngine = Camera2EngineImpl(context, engineScope)

            // ViewModel sẽ nhận engine và có trách nhiệm hủy scope khi nó bị hủy
            return CameraViewModel(engine, engineScope) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}