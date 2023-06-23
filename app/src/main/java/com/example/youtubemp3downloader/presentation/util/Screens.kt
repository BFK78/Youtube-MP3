package com.example.youtubemp3downloader.presentation.util

sealed class Screens(val title: String, val route: String) {
    object InitialScreen : Screens(title = "Initial Screen", route = "initial_screen")
    object GrabbingScreen : Screens(title = "Grabbing Screen", route = "grabbing_screen")
}
