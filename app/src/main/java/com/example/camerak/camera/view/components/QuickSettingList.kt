package com.example.camerak.camera.view.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.key
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.camerak.R
import com.example.camerak.camera.model.camerametadata.AspectRatioValue
import com.example.camerak.camera.model.camerametadata.FlashValue
import com.example.camerak.camera.model.camerametadata.SettingKey
import com.example.camerak.camera.viewmodel.CameraViewModel
import com.example.camerak.camera.viewmodel.QuickSettingItemState
import com.example.camerak.camera.viewmodel.QuickSettingsUiState
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.toList

@Composable
fun QuickSettingsList(
    state: QuickSettingsUiState,
    onSettingClicked: (SettingKey) -> Unit
) {
    Row(modifier = Modifier
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
        horizontalArrangement = Arrangement.SpaceEvenly) {
        state.settingItems.forEach { itemState ->
            QuickSettingItem(
                state = itemState,
                onClick = { onSettingClicked(itemState.key) }
            )
        }
    }
}


// QuickSettingItem giờ nhận một state object hoàn chỉnh
@Composable
fun QuickSettingItem(
    state: QuickSettingItemState<*>, // Dùng wildcard vì không quan tâm kiểu T cụ thể
    onClick: () -> Unit
) {
    // Lấy icon từ giá trị hiện tại

    Log.i("trung.dam", "QuickSettingItem: ${state.javaClass}")
    IconButton(onClick = { onClick }) {
        Image(painter = painterResource(id = state.currentValue.iconRes?: R.drawable.ic_launcher_background), contentDescription = "Flash")
    }
}