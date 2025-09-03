package com.example.camerak.camera.model.camerametadata

import androidx.annotation.DrawableRes
import com.example.camerak.R

enum class FlashMode { ON, OFF, AUTO }
enum class Ratio { RATIO_4_3, RATIO_16_9 }
enum class FilterMode { NONE, SEPIA, GRAYSCALE } // Ví dụ
enum class TimerDuration { OFF, S3, S10 } // Ví dụ


sealed class SettingKey<T>(val defaultValue: T, val displayName: String) { // Thêm displayName cho key
    object Flash : SettingKey<FlashMode>(FlashMode.OFF, "Flash")
    object AspectRatio : SettingKey<Ratio>(Ratio.RATIO_4_3, "Tỷ lệ")
    object Filter : SettingKey<FilterMode>(FilterMode.NONE, "Bộ lọc") // Ví dụ
    object Timer : SettingKey<TimerDuration>(TimerDuration.OFF, "Hẹn giờ") // Ví dụ

    // Bạn có thể thêm một phương thức để lấy tất cả các key nếu cần
    companion object {
        fun getAllKeys(): List<SettingKey<*>> = listOf(Flash, AspectRatio, Filter, Timer)
    }
}
data class SettingOption<T>( // T ở đây vẫn hữu ích để đảm bảo kiểu khớp với SettingKey
    val value: T,
    val displayName: String,
    @DrawableRes val iconResId: Int? = null
)

// ConfigurableSetting giờ sử dụng SettingKey
data class ConfigurableSetting<T>(
    val key: SettingKey<T>,
    var currentOption: SettingOption<T>,
    val availableOptions: List<SettingOption<T>>,
)


// Ví dụ cập nhật dữ liệu mẫu
object QuickSettingHelper {
    // Định nghĩa các options cho từng SettingKey
    val flashOptions = listOf(
        SettingOption(FlashMode.ON, "Bật", R.drawable.baseline_flash_on_24),
        SettingOption(FlashMode.OFF, "Tắt", R.drawable.flash_off),
        SettingOption(FlashMode.AUTO, "Tự động", R.drawable.flash_auto)
    )

    val ratioOptions = listOf(
        SettingOption(Ratio.RATIO_4_3, "4:3", R.drawable.ratio_4_3),
        SettingOption(Ratio.RATIO_16_9, "16:9", R.drawable.square)
    )

    val timerOptions = listOf(
        SettingOption(TimerDuration.OFF, "Off", R.drawable.timer),
        SettingOption(TimerDuration.S3, "3s", R.drawable.timer_3s),
        SettingOption(TimerDuration.S10, "10s", R.drawable.timer_10s)
        )

    val filterOptions = listOf(
        SettingOption(FilterMode.NONE, "None", R.drawable.timer),
        SettingOption(FilterMode.SEPIA, "Sepia", R.drawable.timer_3s),
        SettingOption(FilterMode.GRAYSCALE, "Grayscale", R.drawable.timer_10s),
    )
    // TODO: Định nghĩa options cho Filter và Timer nếu bạn đã thêm chúng vào SettingKey

    fun initialSettingsMap(): Map<SettingKey<*>, ConfigurableSetting<*>> {
        val flashSetting = ConfigurableSetting(
            key = SettingKey.Flash,
            currentOption = flashOptions.first { it.value == SettingKey.Flash.defaultValue },
            availableOptions = flashOptions,
        )

        val ratioSetting = ConfigurableSetting(
            key = SettingKey.AspectRatio,
            currentOption = ratioOptions.first { it.value == SettingKey.AspectRatio.defaultValue },
            availableOptions = ratioOptions,
        )

        val timerSetting = ConfigurableSetting(
            key = SettingKey.Timer,
            currentOption = timerOptions.first { it.value == SettingKey.Timer.defaultValue },
            availableOptions = timerOptions,
        )

        val filterSetting = ConfigurableSetting(
            key = SettingKey.Filter,
            currentOption = filterOptions.first { it.value == SettingKey.Filter.defaultValue },
            availableOptions = filterOptions,
        )

        return mapOf(
            SettingKey.Flash to flashSetting,
            SettingKey.AspectRatio to ratioSetting,
            SettingKey.Timer to timerSetting,
            SettingKey.Filter to filterSetting
        )
    }
}