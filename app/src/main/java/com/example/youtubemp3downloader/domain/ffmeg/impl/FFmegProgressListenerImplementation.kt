package com.example.youtubemp3downloader.domain.ffmeg.impl

import com.example.youtubemp3downloader.domain.ffmeg.FFmpegProgressListener
import javax.inject.Inject

class FFmegProgressListenerImplementation @Inject constructor() : FFmpegProgressListener {
    override fun onProgress(progress: Int) {
    }
}