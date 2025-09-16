package com.example.camerak.camera.view.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.camerak.R
import com.example.camerak.camera.viewmodel.CameraAction
import com.example.camerak.camera.viewmodel.CameraState
import com.example.camerak.camera.viewmodel.CaptureMode

val shutterIconResourceMap: Map<CaptureMode, Int> = mapOf(
    CaptureMode.PHOTO to R.drawable.shutter,
    CaptureMode.VIDEO to R.drawable.shutter
)
@Composable
fun ShutterButton(
    modifier: Modifier = Modifier,
    cameraState: CameraState,
    onAction: (CameraAction) -> Unit
) {
    IconButton(
        modifier = modifier.size(90.dp),
        onClick = { onAction(CameraAction.ShutterClick) }) {
        Image(painter = painterResource(id = shutterIconResourceMap.get(cameraState.currentMode)?: R.drawable.shutter), contentDescription = "Gallery")
    }
}