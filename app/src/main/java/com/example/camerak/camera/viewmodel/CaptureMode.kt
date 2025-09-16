// viewmodel/CaptureMode.kt
package com.example.camerak.camera.viewmodel

enum class CaptureMode { PHOTO, VIDEO }
enum class PreviewState {IDLE, PREVIEWING, STOPPED, STARTING, STOPPING}

enum class CaptureState {CAPTURE_READY, CAPTURE_REQUESTED, RECORD_READY, RECORD_STARTED, RECORD_REQUESTED}

enum class CameraFacing{BACK, FRONT}
