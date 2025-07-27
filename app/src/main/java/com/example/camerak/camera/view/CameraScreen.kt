package com.example.camerak.camera.view

import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.camerak.camera.view.components.BottomControls
import com.example.camerak.camera.view.components.QuickSettingsList
import com.example.camerak.camera.view.components.ShootingModeList
import com.example.camerak.camera.viewmodel.CameraAction
import com.example.camerak.camera.viewmodel.CameraViewModel
import com.example.camerak.camera.viewmodel.CameraViewModelFactory
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen() {
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)

    if (cameraPermissionState.status.isGranted) {
        // Nếu đã có quyền, hiển thị màn hình camera
        CameraView()
    } else {
        // Nếu chưa có quyền, hiển thị màn hình yêu cầu quyền
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                Text("Yêu cầu quyền truy cập Camera")
            }
        }
    }
}

@Composable
fun CameraView() {
    val viewModel: CameraViewModel = viewModel(
        factory = CameraViewModelFactory(LocalContext.current.applicationContext)
    )
    val cameraState by viewModel.cameraState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        // Vùng hiển thị Camera Preview
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(3f / 4f),
            factory = { context ->
                SurfaceView(context).apply {
                    holder.addCallback(object : SurfaceHolder.Callback {
                        override fun surfaceCreated(holder: SurfaceHolder) {
                            viewModel.onAction(
                                CameraAction.PreviewSurfaceReady(holder)
                            )
                        }
                        override fun surfaceDestroyed(holder: SurfaceHolder) = Unit
                        override fun surfaceChanged(holder: SurfaceHolder, f: Int, w: Int, h: Int) {
                            viewModel.onAction(CameraAction.PreviewSizeChanged(w, h))
                        }
                    })
                }
            }
        )

        // Lớp phủ chứa các điều khiển
        Column(modifier = Modifier.fillMaxSize()) {
            QuickSettingsList()
            Spacer(modifier = Modifier.weight(1f))
            ShootingModeList(
                currentMode = cameraState.currentMode,
                onModeChange = { viewModel.setCaptureMode(it) }
            )
            BottomControls(
                cameraState = cameraState,
                onAction = viewModel::onAction
            )
        }
    }
}