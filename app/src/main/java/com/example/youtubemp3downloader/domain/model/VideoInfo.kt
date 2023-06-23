package com.example.youtubemp3downloader.domain.model

data class VideoInfo(
    val videoTitle: String = "",
    val thumbnailUrl: String = "",
    val viewCount: String? = null,
    val likeCount: String? = null,
    val sourceURL: String? = null
)