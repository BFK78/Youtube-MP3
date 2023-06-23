package com.example.youtubemp3downloader.domain.model

sealed class CurrentAction(val message: String = "") {
    object Download : CurrentAction()
    object Converting : CurrentAction()
    object Saving : CurrentAction()
    class Failed(message: String) : CurrentAction(message)
    class Success(message: String) : CurrentAction(message)
}
