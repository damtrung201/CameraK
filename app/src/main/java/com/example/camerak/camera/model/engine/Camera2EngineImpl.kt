package com.yourcompany.camerapp.camera.model.engine

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CaptureRequest
import android.media.Image
import android.media.ImageReader
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import android.view.Surface
import android.view.SurfaceHolder
import com.example.camerak.camera.model.camerametadata.CameraInfo
import com.example.camerak.camera.model.camerametadata.CameraSettings
import com.example.camerak.camera.model.engine.CameraEngine
import com.example.camerak.camera.model.engine.core.CameraDeviceManager
import com.example.camerak.camera.model.engine.core.CameraSessionManager
import com.example.camerak.camera.viewmodel.CaptureMode
import com.yourcompany.camerapp.camera.model.engine.core.CaptureRequestBuilder
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

@SuppressLint("MissingPermission")
class Camera2EngineImpl(
    private val context: Context,
    private val coroutineScope: CoroutineScope
) : CameraEngine {
    private val sensorManager: SensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val cameraSettings = CameraSettings()
    private val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    private val deviceManager = CameraDeviceManager(context, cameraManager)
    private val sessionManager = CameraSessionManager()
    private var requestBuilder: CaptureRequestBuilder? = null
    private var camDevice: CameraDevice? = null
    private var surfaceHolder: SurfaceHolder? = null
    private var previewCaptureRequest: CaptureRequest? = null
    private var takePictureCaptureRequest: CaptureRequest? = null
    private var recordingCaptureRequest: CaptureRequest? = null
    private var imageReader: ImageReader? = null
    private var mediaRecorder: MediaRecorder? = null
    private var captureSession: CameraCaptureSession? = null
    private var cameraFacing: Int = CameraCharacteristics.LENS_FACING_BACK
    private var captureMode: CaptureMode = CaptureMode.PHOTO
    val captureSessionStateCallback: CameraCaptureSession.StateCallback = object : CameraCaptureSession.StateCallback() {
        override fun onConfigureFailed(p0: CameraCaptureSession) {
            TODO("Not yet implemented")
        }

        override fun onConfigured(p0: CameraCaptureSession) {
            Log.i("trung.dam", "onConfigured: ")
            captureSession = p0
            previewCaptureRequest = requestBuilder!!.buildPreviewRequest(surfaceHolder!!.surface)
            if(captureMode == CaptureMode.PHOTO) {
                takePictureCaptureRequest =
                    requestBuilder!!.buildStillCaptureRequest(imageReader!!.surface)
            } else {
                recordingCaptureRequest =
                    requestBuilder!!.buildRecordingRequest(mediaRecorder!!.surface, surfaceHolder!!.surface)
            }
            previewCaptureRequest?.let { captureSession?.setRepeatingRequest(it, null, null) }
        }

    }
    val cameraDeviceStateCallback: CameraDevice.StateCallback = object : CameraDevice.StateCallback() {
        override fun onDisconnected(p0: CameraDevice) {
            TODO("Not yet implemented")
        }

        override fun onError(p0: CameraDevice, p1: Int) {
            TODO("Not yet implemented")
        }

        override fun onOpened(p0: CameraDevice) {
            Log.i("trung.dam", "onOpened: ")
           camDevice = p0
            requestBuilder = CaptureRequestBuilder(p0)
            if(captureMode == CaptureMode.PHOTO) {
                camDevice?.createCaptureSession(
                    listOf(
                        surfaceHolder?.surface,
                        imageReader?.surface
                    ), captureSessionStateCallback, null
                )
            }
            else {
                camDevice?.createCaptureSession(
                    listOf(
                        surfaceHolder?.surface,
                        mediaRecorder?.surface
                    ), captureSessionStateCallback, null
                )
            }
        }

    }

    // THAY ĐỔI: Nhận SurfaceHolder thay vì Surface
    override suspend fun initializeCamera(previewHolder: SurfaceHolder) {
        surfaceHolder = previewHolder
        openCamera(cameraFacing)
    }

    // Hàm mới để chọn kích thước
    private fun chooseOptimalPreviewSize(supportedSizes: Array<Size>): Size {
        val targetRatio = 3.0 / 4.0
        return supportedSizes.filter {
            abs((it.width.toDouble() / it.height) - targetRatio) < 0.1
        }.maxByOrNull { it.height * it.width } ?: supportedSizes[0]
    }

    override fun onPreviewSizeChanged(width: Int, height: Int) {
        // TODO: Handle preview size changes
    }

    override fun switchCamera() {
        closeCamera()
        if(cameraFacing == CameraCharacteristics.LENS_FACING_BACK){
            cameraFacing = CameraCharacteristics.LENS_FACING_FRONT
        } else {
            cameraFacing = CameraCharacteristics.LENS_FACING_BACK
        }
        openCamera(cameraFacing)
    }

    override fun takePicture() {
        Log.i("trung.dam", "takePicture: ")
        captureSession?.captureBurst(listOf(takePictureCaptureRequest), null, null)
    }

    override fun recordVideo() {
        captureSession?.setRepeatingBurst(listOf(recordingCaptureRequest), null, null)
        mediaRecorder?.start()
    }

    override fun changeMode(mode: CaptureMode) {
        closeCamera()
        captureMode = mode
        openCamera(cameraFacing)
    }

    private fun openCamera(facing: Int){
        Log.i("trung.dam", "openCamera: ")
        val cameraIdList = cameraManager.cameraIdList
        var cameraId: String? = null
        for(id in cameraIdList){
            val characteristics = cameraManager.getCameraCharacteristics(id)
            if(characteristics.get(CameraCharacteristics.LENS_FACING) == facing){
                cameraId = id
                break
            }
        }
        if(cameraId == null){
            return;
        }
        val characteristics = cameraManager.getCameraCharacteristics(cameraId)
        val configMap = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)!!

        val previewSize = chooseOptimalPreviewSize(configMap.getOutputSizes(SurfaceHolder::class.java))
        surfaceHolder?.setFixedSize(previewSize.width, previewSize.height)
        if(captureMode == CaptureMode.PHOTO) {
            imageReader =
                ImageReader.newInstance(previewSize.width, previewSize.height, ImageFormat.JPEG, 2)
            imageReader?.setOnImageAvailableListener(object : ImageReader.OnImageAvailableListener {
                override fun onImageAvailable(reader: ImageReader?) {
                    Log.i("trung.dam", "onImageAvailable: ")
                    val image = reader?.acquireNextImage()
                    if (image == null) {
                        return
                    }
                    coroutineScope.launch {
                        saveImageFromReaderToGallery(context, image, "MyCameraApp")
                    }

                }
            }, null)
        } else {
            setupMediaRecorder(previewSize)
        }
        coroutineScope.launch {
            cameraManager.openCamera(cameraId, cameraDeviceStateCallback, null)
        }

    }

    suspend fun saveImageFromReaderToGallery(context: Context, image: Image, appName: String): Uri? {
        // 1. Trích xuất dữ liệu từ Image thành ByteArray
        // Giả sử ảnh là định dạng JPEG, dữ liệu nằm trong plane đầu tiên.
        val buffer: ByteBuffer = image.planes[0].buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)

        // 2. Chuẩn bị thông tin ảnh để lưu vào MediaStore
        val fileName = "IMG_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}.jpg"
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/$appName")
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }
        }

        val contentResolver = context.contentResolver
        var imageUri: Uri? = null

        try {
            // 3. Tạo một mục mới trong MediaStore và lấy Uri
            imageUri =
                contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

            imageUri?.let { uri ->
                // 4. Mở một luồng ghi tới Uri đó và chép dữ liệu byte vào
                contentResolver.openOutputStream(uri)?.use { outputStream ->
                    outputStream.write(bytes)
                }

                // 5. Cập nhật flag IS_PENDING để báo cho hệ thống biết file đã ghi xong
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    contentValues.clear()
                    contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                    contentResolver.update(uri, contentValues, null, null)
                }

                println("✅ Lưu ảnh thành công: $uri")
                return uri
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Nếu có lỗi, xóa bản ghi đã tạo trong MediaStore (nếu có)
            imageUri?.let { contentResolver.delete(it, null, null) }
        }

        println("❌ Lưu ảnh thất bại.")
        return null
    }

    private fun setupMediaRecorder(size: Size) {
        mediaRecorder = MediaRecorder(context)

        // Lấy đường dẫn file để lưu video
        val outputFile = createVideoFile(context) // Hàm helper để tạo file .mp4

        mediaRecorder?.apply {
            // THỨ TỰ CẤU HÌNH RẤT QUAN TRỌNG
            // 1. Set nguồn âm thanh và video
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setVideoSource(MediaRecorder.VideoSource.SURFACE) // Lấy video từ một Surface

            // 2. Set định dạng đầu ra
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)

            // 3. Set đường dẫn file output
            setOutputFile(outputFile.absolutePath)

            // 4. Set bộ mã hóa (encoder) và các thông số khác
            setVideoEncoder(MediaRecorder.VideoEncoder.H264)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setVideoEncodingBitRate(10000000) // Tốc độ bit, ví dụ 10Mbps
            setVideoFrameRate(30) // Tốc độ khung hình
            setVideoSize(size.width, size.height) // Kích thước video
            // setOrientationHint(rotation) // Hướng của video
        }
        mediaRecorder?.prepare()
    }
    private fun closeCamera(){
        captureSession?.stopRepeating()
        camDevice?.close()
    }
    fun createVideoFile(context: Context): File {
        // Tạo tên file duy nhất dựa trên thời gian hiện tại
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val fileName = "VIDEO_${timeStamp}.mp4"

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // --- Cách làm cho Android 10 (API 29) trở lên ---
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
                // Lưu file vào thư mục Movies/AppName
                put(MediaStore.MediaColumns.RELATIVE_PATH, "${Environment.DIRECTORY_MOVIES}/MyCameraApp")
            }

            val contentResolver = context.contentResolver
            val uri = contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues)
                ?: throw IOException("Không thể tạo file video trong MediaStore")

            // Mở một file descriptor và tạo đối tượng File từ nó
            val pfd = contentResolver.openFileDescriptor(uri, "w")
                ?: throw IOException("Không thể mở file descriptor")

            // MediaRecorder không thể ghi trực tiếp vào FileDescriptor qua đường dẫn,
            // nhưng nhiều thư viện hoặc code native có thể.
            // Để đơn giản, ta sẽ tạo một File object trỏ tới đường dẫn tạm thời
            // mà MediaStore quản lý.
            // Chú ý: Đây là một cách "lách" để lấy đường dẫn File.
            // Cách an toàn nhất là dùng `openFileDescriptor` và truyền `fileDescriptor` cho MediaRecorder.
            File("/proc/self/fd/${pfd.fd}")

        } else {
            // --- Cách làm cho Android cũ hơn (API < 29) ---
            val mediaStorageDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), "MyCameraApp")

            // Tạo thư mục nếu nó chưa tồn tại
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    throw IOException("Không thể tạo thư mục lưu trữ")
                }
            }
            File("${mediaStorageDir.path}${File.separator}$fileName")
        }
    }
}