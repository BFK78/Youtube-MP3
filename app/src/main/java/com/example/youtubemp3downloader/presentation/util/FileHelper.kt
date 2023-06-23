package com.example.youtubemp3downloader.presentation.util

import android.os.Environment
import androidx.documentfile.provider.DocumentFile

object FileHelper {

    fun getDirectoryPath(documentFile: DocumentFile?): String {
        var currentDocument = documentFile
        val pathComponents = mutableListOf<String>()

        while (currentDocument != null) {
            val displayName = currentDocument.name
            if (!displayName.isNullOrEmpty()) {
                pathComponents.add(0, displayName)
            }
            currentDocument = currentDocument.parentFile
        }

        val externalStoragePath = Environment.getExternalStorageDirectory().path
        val absolutePath = pathComponents.joinToString("/", prefix = "$externalStoragePath/")

        return absolutePath
    }
}