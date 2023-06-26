package com.example.youtubemp3downloader.presentation.viewmodel

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arthenica.ffmpegkit.FFmpegKit
import com.chaquo.python.Python
import com.example.youtubemp3downloader.domain.model.CurrentAction
import com.example.youtubemp3downloader.domain.model.VideoInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject


@HiltViewModel
class GrabbingScreenViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
) : ViewModel() {

    var totalFileSize by mutableStateOf("")
        private set

    var downloadedSize by mutableStateOf("")
        private set

    var progress by mutableStateOf(0f)
        private set

    var youtubeLink by mutableStateOf("https://www.youtube.com/watch?v=TQQlZhbC5ps")
        private set

    var filePath by mutableStateOf("")
        private set

    var videoInformation by mutableStateOf(VideoInfo())
        private set

    private var downloadLocation by mutableStateOf<File?>(null)

    private var videoLocation by mutableStateOf<File?>(null)

    var currentAction by mutableStateOf<CurrentAction>(CurrentAction.Download)
        private set

    private var saveMP3File by mutableStateOf<File?>(null)

    fun onYoutubeLinkChange(value: String) {
        youtubeLink = value
    }

    fun onFilePathChanged(value: String) {
        filePath = value
    }

    fun onVideoLocationChange(file: File) {
        videoLocation = file
    }

    fun changeCurrentAction(action: CurrentAction) {
        currentAction = action
    }

    suspend fun getVideoInformation(): Boolean = withContext(Dispatchers.IO) {
        try {
            val py = Python.getInstance()
            val videoInfoModule = py.getModule("video_info")

            val result = videoInfoModule.callAttr(
                "extract_video_info",
                youtubeLink
            )
            result?.let {

                val resultMap = result.toString().split(", ")

                val valueMap = mutableMapOf<String, String>()

                resultMap.forEach { data ->
                    val (key, value) = data.split(": ")
                    val cleanedKey = key.trim().removeSurrounding("'")
                    val cleanedValue = value.trim().removeSurrounding("'").removeSuffix("}")
                    valueMap[cleanedKey] = cleanedValue
                }

                val videoInfo = VideoInfo(
                    videoTitle = valueMap["{'video_title'"].toString(),
                    thumbnailUrl = valueMap["thumbnail_url"].toString(),
                    viewCount = valueMap["view_count"]?.toLong()?.formatCount(),
                    likeCount = valueMap["like_count"]?.toLong()?.formatCount(),
                    sourceURL = valueMap["video_url"].toString().removeSurrounding("'")
                )
                videoInformation = videoInfo
                true
            } ?: run {
                false
            }
        } catch (exception: Exception) {
            false
        }
    }

    private fun Long.formatCount(): String {

        val suffixes = listOf("", "K", "M", "B", "T")
        var formattedCount = this.toDouble()
        var suffixIndex = 0

        while (formattedCount >= 1000 && suffixIndex < suffixes.size - 1) {
            formattedCount /= 1000
            suffixIndex++
        }

        val decimalFormat = if (formattedCount % 1 == 0.0) {
            "%.0f"
        } else {
            "%.1f"
        }

        return String.format(decimalFormat, formattedCount) + suffixes[suffixIndex]
    }


    fun downloadFile() = viewModelScope.launch(Dispatchers.IO) {
        try {
            currentAction = CurrentAction.Download

            val client = OkHttpClient()
            val request = Request.Builder()
                .url(videoInformation.sourceURL!!)
                .build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody = response.body
                responseBody?.let {
                    val totalBytes = it.contentLength()
                    var downloadedBytes: Long = 0
                    totalFileSize = convertBytesToMB(totalBytes)

                    val file = File(
                        context.getExternalFilesDir(null),
                        "${videoInformation.videoTitle.trim()}.mp4"
                    )
                    onVideoLocationChange(file)
                    val outputStream = FileOutputStream(file)

                    outputStream.use { output ->
                        val buffer = ByteArray(8192)
                        var bytesRead: Int
                        while (it.source().read(buffer).also { bytesRead = it } != -1) {
                            output.write(buffer, 0, bytesRead)
                            downloadedBytes += bytesRead
                            downloadedSize = convertBytesToMB(downloadedBytes)
                            progress =
                                ((downloadedBytes.toDouble() / totalBytes.toDouble()))
                                    .toFloat()
                        }
                        output.flush()
                    }
                }
            }

        } catch (exception: Exception) {
            exception.printStackTrace()
            currentAction = CurrentAction.Failed(exception.message ?: "Something went wrong!")
        }
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    fun moveFileToExternalStorageAndroidQ(contentResolver: ContentResolver) =
        viewModelScope.launch {
            progress = 0f
            totalFileSize = ""
            downloadedSize = ""
            delay(3000)

            val sourceFile = saveMP3File!!
            val destinationDirectory =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
            val fileName = sourceFile.name

            if (!sourceFile.exists() || !sourceFile.isFile) {
                return@launch
            }

            try {
                if (!destinationDirectory.exists()) {
                    destinationDirectory.mkdirs()
                }

                val values = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_MUSIC)
                }

                val externalContentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                val insertUri = contentResolver.insert(externalContentUri, values)
                if (insertUri != null) {
                    val outputStream = contentResolver.openOutputStream(insertUri)
                    outputStream?.use { output ->
                        val inputStream = FileInputStream(sourceFile)
                        val buffer = ByteArray(1024)
                        var bytesRead: Int
                        var downloadedBytes: Long = 0
                        while (inputStream.read(buffer).also { bytesRead = it } > 0) {
                            output.write(buffer, 0, bytesRead)
                            downloadedBytes += bytesRead
                            progress =
                                (downloadedBytes.toDouble() / sourceFile.length()
                                    .toDouble()).toFloat()
                        }
                        inputStream.close()
                        output.flush()
                    }

                    sourceFile.delete()
                    return@launch
                } else {
                    currentAction = CurrentAction.Failed("Failed to create file in MediaStore.")
                    return@launch
                }
            } catch (e: IOException) {
                e.printStackTrace()
                currentAction = CurrentAction.Failed(e.message ?: "Something went wrong!")
                return@launch
            }
        }


    suspend fun moveFileToExternalStorage() = viewModelScope.launch {

        progress = 0f
        totalFileSize = ""
        downloadedSize = ""
        delay(3000)


        val sourceFile = saveMP3File!!

        val destinationDirectory = File(filePath)

        if (!sourceFile.exists() || !sourceFile.isFile) {
            return@launch
        }

        if (!destinationDirectory.exists()) {
            destinationDirectory.mkdirs()
        }

        val destinationFilePath =
            destinationDirectory.absolutePath + File.separator + sourceFile.name

        try {
            withContext(Dispatchers.IO) {
                val inputStream = FileInputStream(sourceFile)
                val outputStream = FileOutputStream(destinationFilePath)

                val buffer = ByteArray(1024)
                var bytesRead: Int
                var downloadedBytes: Long = 0

                while (inputStream.read(buffer).also { bytesRead = it } > 0) {
                    outputStream.write(buffer, 0, bytesRead)
                    downloadedBytes += bytesRead
                    progress =
                        ((downloadedBytes.toDouble() / sourceFile.length().toDouble()))
                            .toFloat()
                }

                inputStream.close()
                outputStream.close()

                sourceFile.delete()
            }

            return@launch
        } catch (e: IOException) {
            e.printStackTrace()
            currentAction = CurrentAction.Failed(e.message ?: "Something went wrong!")
            return@launch
        }
    }

    fun convertToMp3(): Boolean {
        currentAction = CurrentAction.Converting
        downloadedSize = ""
        totalFileSize = ""
        progress = 0f
        val job = setConvertProgress()
        val outputFilePath =
            File(context.getExternalFilesDir(null), "${videoInformation.videoTitle}.mp3")

        saveMP3File = outputFilePath

        return try {
            val ffmpegCommand =
                "-i \"${videoLocation?.absolutePath}\" -vn -acodec libmp3lame -q:a 4 \"${outputFilePath.absolutePath}\""

            FFmpegKit.executeAsync(ffmpegCommand) { session ->
                job.cancel()
                progress = 1f
            }

            true
        } catch (e: Exception) {
            currentAction = CurrentAction.Failed(e.message ?: "Something went wrong!")
            false
        }
    }

    private fun setConvertProgress() = viewModelScope.launch {
        while (true) {
            delay(1000)
            if (progress < 0.7f) {
                progress += .02f
            }
        }
    }

    private fun convertBytesToMB(bytes: Long): String {
        val megabytes = bytes.toDouble() / (1024 * 1024)
        return String.format("%.2f MB", megabytes)
    }

    fun onSuccess() {
        currentAction = CurrentAction.Success("MP3 successfully saved into selected folder")
    }

    fun onResetButtonClick() {
        progress = 0f
        totalFileSize = ""
        downloadedSize = ""
        youtubeLink = ""
        filePath = ""
        saveMP3File = null
        videoInformation = VideoInfo()
        downloadLocation = null
        videoLocation = null
        currentAction = CurrentAction.Download
    }

}




