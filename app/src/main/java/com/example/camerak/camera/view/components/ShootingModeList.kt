package com.example.camerak.camera.view.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.camerak.camera.viewmodel.CameraAction
import com.example.camerak.camera.viewmodel.CameraState
import com.example.camerak.camera.viewmodel.CaptureMode

@Composable
fun ShootingModeList(
    modifier: Modifier = Modifier,
    cameraState: CameraState,
    onModeChange: (CameraAction) -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(bottom = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CaptureMode.values().forEach { mode ->
            val isSelected = mode == cameraState.currentMode
            Text(
                text = mode.name,
                color = if (isSelected) Color.Yellow else Color.White,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                modifier = Modifier.clickable { onModeChange(CameraAction.ChangeMode) }
            )
        }
    }
}