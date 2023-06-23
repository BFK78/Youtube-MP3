package com.example.youtubemp3downloader.domain.ffmeg

interface FFmpegProgressListener {
    fun onProgress(progress: Int)
}