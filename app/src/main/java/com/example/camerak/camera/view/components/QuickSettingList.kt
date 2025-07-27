package com.example.camerak.camera.view.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.camerak.R

@Composable
fun QuickSettingsList(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth().background(Color.Black.copy(alpha = 0.3f)).padding(8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        IconButton(onClick = { /* onSettingChange(...) */ }) {
            Image(painter = painterResource(id = R.drawable.ic_launcher_foreground), contentDescription = "Flash")
        }
        IconButton(onClick = { /* onSettingChange(...) */ }) {
            Image(painter = painterResource(id = R.drawable.ic_launcher_foreground), contentDescription = "Aspect Ratio")
        }
    }
}