package com.example.camerak.camera.view.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.key
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.dp
import androidx.preference.forEach
import com.example.camerak.R
import com.example.camerak.camera.viewmodel.CameraViewModel
import kotlinx.coroutines.flow.forEach

@Composable
fun QuickSettingsList(modifier: Modifier = Modifier,
                      cameraViewModel: CameraViewModel) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.3f))
            .padding(
                top = 20.dp,
                bottom = 10.dp
            )
            .border(
                width = 2.dp,
                color = Color.White,
                shape = RoundedCornerShape(8.dp)
            )
            ,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        cameraViewModel.quickSettings.collect { setting ->
            // Icon hiển thị trong QuickSettingsList luôn là icon của currentOption
            val currentIconToShow = setting.currentOption.iconResId // Đảm bảo non-null

            IconButton(onClick = { onSettingClicked(setting.key) }) {
                Image(
                    painter = painterResource(id = currentIconToShow),
                    contentDescription = setting.contentDescription,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
        IconButton(onClick = { /* onSettingChange(...) */ }) {
            Image(painter = painterResource(id = R.drawable.baseline_flash_on_24), contentDescription = "Flash")
        }
        IconButton(onClick = { /* onSettingChange(...) */ }) {
            Image(painter = painterResource(id = R.drawable.baseline_photo_filter_24), contentDescription = "Aspect Ratio")
        }
        IconButton(onClick = { /* onSettingChange(...) */ }) {
            Image(painter = painterResource(id = R.drawable.ratio_4_3), contentDescription = "Aspect Ratio")
        }
        IconButton(onClick = { /* onSettingChange(...) */ }) {
            Image(painter = painterResource(id = R.drawable.timer), contentDescription = "Aspect Ratio")
        }
    }
}