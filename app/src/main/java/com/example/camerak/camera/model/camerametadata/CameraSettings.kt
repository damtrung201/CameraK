// model/camerametadata/CameraSettings.kt
package com.example.camerak.camera.model.camerametadata

class CameraSettings {
    private val values: MutableMap<SettingKey<*>, Any> = mutableMapOf()

    fun <T> set(key: SettingKey<T>, value: T) {
        values[key] = value as Any
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> get(key: SettingKey<T>): T {
        return (values[key] as? T) ?: key.defaultValue
    }

    fun copy(): CameraSettings {
        val newSettings = CameraSettings()
        newSettings.values.putAll(this.values)
        return newSettings
    }
}