package com.example.youtubemp3downloader.di

import com.example.youtubemp3downloader.domain.ffmeg.FFmpegProgressListener
import com.example.youtubemp3downloader.domain.ffmeg.impl.FFmegProgressListenerImplementation
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface CommonModule {

    @Binds
    fun bindFFmegProgressListener(fFmegProgressListenerImplementation: FFmegProgressListenerImplementation): FFmpegProgressListener

}