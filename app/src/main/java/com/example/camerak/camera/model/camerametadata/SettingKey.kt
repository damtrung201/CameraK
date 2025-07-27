// model/camerametadata/SettingKey.kt
package com.example.camerak.camera.model.camerametadata

sealed class SettingKey<T>(val defaultValue: T) {
    object Flash : SettingKey<FlashMode>(FlashMode.OFF)
    object AspectRatio : SettingKey<Ratio>(Ratio.RATIO_4_3)
    object IsFrontCamera : SettingKey<Boolean>(false)
}
enum class FlashMode { ON, OFF, AUTO }
enum class Ratio { RATIO_4_3, RATIO_16_9 }