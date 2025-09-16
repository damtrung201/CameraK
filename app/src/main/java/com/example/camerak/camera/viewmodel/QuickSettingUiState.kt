package com.example.camerak.camera.viewmodel

import com.example.camerak.camera.model.camerametadata.AspectRatioValue
import com.example.camerak.camera.model.camerametadata.FlashValue
import com.example.camerak.camera.model.camerametadata.SettingKey
import com.example.camerak.camera.model.camerametadata.SettingKey.*
import com.example.camerak.camera.model.camerametadata.SettingValue

data class QuickSettingsUiState(
    val settingItems: List<QuickSettingItemState<*>> = listOf(QuickSettingItemState<FlashValue>(Flash, FlashValue.OFF, listOf(FlashValue.OFF, FlashValue.ON, FlashValue.AUTO)))
)

data class QuickSettingItemState<T : SettingValue>(
    val key: SettingKey,
    val currentValue: T,
    val availableValues: List<T>, // Danh sách tất cả các giá trị có thể có
    val isEnabled: Boolean = true
)