// model/camerametadata/CameraSettings.kt
package com.example.camerak.camera.model.camerametadata

import androidx.annotation.DrawableRes
import androidx.compose.runtime.mutableStateMapOf
import com.example.camerak.R

class CameraSettings(){
    val settingsMap: MutableMap<SettingKey, SettingValue> = mutableMapOf(
        SettingKey.Flash to FlashValue.OFF,
        SettingKey.AspectRatio to AspectRatioValue.RATIO_4_3,
        SettingKey.Timer to TimerValue.OFF,
        SettingKey.Filter to FilterValue.OFF
    )

    fun applySetting(key: SettingKey, value: SettingValue) {
        settingsMap.put(key, value)
    }

    fun getSetting(key: SettingKey): SettingValue {
        return settingsMap.get(key)!!
    }
}

sealed class SettingKey {
    object Flash : SettingKey()
    object AspectRatio : SettingKey()
    object Timer : SettingKey()
    object Filter : SettingKey()
}

// Đại diện cho giá trị của một setting. Có thể có nhiều loại giá trị khác nhau.
sealed interface SettingValue {
    val iconRes: Int?
}

enum class FlashValue(
    @DrawableRes override val iconRes: Int
) : SettingValue {
    ON(R.drawable.baseline_flash_on_24),
    OFF(R.drawable.flash_off),
    AUTO(R.drawable.flash_auto)
}


enum class AspectRatioValue(
    @DrawableRes override val iconRes: Int
) : SettingValue {
    RATIO_4_3(R.drawable.ratio_4_3),
    RATIO_16_9(R.drawable.ratio_4_3)
}


enum class TimerValue(
    @DrawableRes override val iconRes: Int
) : SettingValue {
    OFF(R.drawable.ratio_4_3),
    TIMER_3S(R.drawable.ratio_4_3),
    TIMER_5S(R.drawable.ratio_4_3),
    TIMER_10S(R.drawable.ratio_4_3)
}

enum class FilterValue(
    @DrawableRes override val iconRes: Int
) : SettingValue {
    OFF(R.drawable.ratio_4_3),
    FILTER_1(R.drawable.ratio_4_3),
    FILTER_2(R.drawable.ratio_4_3),
    FILTER_3(R.drawable.ratio_4_3)
}