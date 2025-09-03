package com.example.camerak.camera.view.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.camerak.camera.viewmodel.CameraAction
import com.example.camerak.camera.viewmodel.CameraState
import com.example.camerak.R
import com.example.camerak.camera.viewmodel.CaptureMode
@Composable
fun BottomControls(
    modifier: Modifier = Modifier,
    cameraState: CameraState,
    onAction: (CameraAction) -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            modifier = Modifier.size(70.dp).padding(start = 20.dp),
            onClick = { /* onAction(CameraAction.OpenGallery) */ }) {
            Image(painter = painterResource(id = R.drawable.gallery), contentDescription = "Gallery")
        }

        val currentMode = cameraState.currentMode
        IconButton(
            modifier = Modifier.size(90.dp),
            onClick = {
                val action = if (currentMode == CaptureMode.PHOTO) CameraAction.TakePhoto else CameraAction.ToggleRecording
                onAction(action)
            }
        ) {
            val shutterIcon = R.drawable.shutter
            Image(modifier = Modifier.fillMaxWidth(),painter = painterResource(id = shutterIcon), contentDescription = "Action Button")
        }

        IconButton(
            modifier = Modifier.size(70.dp).padding(end = 20.dp),
            onClick = { onAction(CameraAction.SwitchCamera) }) {
            Image(painter = painterResource(id = R.drawable.switch_camera), contentDescription = "Switch Camera")
        }
    }
}