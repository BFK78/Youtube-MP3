package com.example.youtubemp3downloader.presentation.grabbing_screen

import android.os.Build
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.youtubemp3downloader.R
import com.example.youtubemp3downloader.domain.model.CurrentAction
import com.example.youtubemp3downloader.presentation.common.CustomButton
import com.example.youtubemp3downloader.presentation.common.CustomProgress
import com.example.youtubemp3downloader.presentation.common.CustomVideoCard
import com.example.youtubemp3downloader.presentation.ui.theme.PlusJakartaSans
import com.example.youtubemp3downloader.presentation.viewmodel.GrabbingScreenViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GrabbingScreen(
    navHostController: NavHostController,
    viewModel: GrabbingScreenViewModel
) {
    val videoInformation = viewModel.videoInformation

    val context = LocalContext.current

    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = true) {
        viewModel.downloadFile()
    }

    Scaffold(
        modifier = Modifier
            .padding(horizontal = 24.dp, vertical = 32.dp),
        topBar = {
            Row(
                modifier = Modifier,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { /*TODO*/ },
                    modifier = Modifier
                        .size(48.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.youtube_icon),
                        contentDescription = "Youtube Icon",
                        tint = Color.Unspecified,
                        modifier = Modifier
                            .padding(top = 4.dp)
                    )
                }

                Spacer(
                    modifier = Modifier
                        .width(8.dp)
                )

                Text(
                    text = "MP3 Downloader",
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    fontFamily = PlusJakartaSans
                )
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            Spacer(
                modifier = Modifier.height(32.dp)
            )

            CustomVideoCard(
                videoTitle = videoInformation.videoTitle,
                modifier = Modifier,
                views = videoInformation.viewCount.toString(),
                likes = videoInformation.likeCount.toString(),
                thumbnailUrl = videoInformation.thumbnailUrl
            )

            Spacer(modifier = Modifier.height(24.dp))

            when (viewModel.currentAction) {
                CurrentAction.Download -> CustomProgress(
                    progressText = "Downloading...",
                    progressValue = "${viewModel.downloadedSize}/${viewModel.totalFileSize}",
                    progress = viewModel.progress,
                    progressColor = Color(0XFF3690FA)
                ) {
                    viewModel.convertToMp3()
                }

                CurrentAction.Converting -> CustomProgress(
                    progressText = "Converting...",
                    progressValue = "",
                    progress = viewModel.progress,
                    progressColor = Color(0XFFFFBB0E)
                ) {
                    scope.launch {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                            viewModel.moveFileToExternalStorageAndroidQ(context.contentResolver)
                        else
                            viewModel.moveFileToExternalStorage()
                    }
                }

                CurrentAction.Saving -> CustomProgress(
                    progressText = "Saving...",
                    progressValue = "${(viewModel.progress * 100).toInt()}%",
                    progress = viewModel.progress,
                    progressColor = Color(0XFF28C6D0)
                ) {
                    viewModel.onSuccess()
                }

                is CurrentAction.Failed -> CustomProgress(
                    progressText = "Failed",
                    progressValue = "❌",
                    progress = 1f,
                    progressColor = Color(0XFFDC4A4A),
                    message = viewModel.currentAction.message
                ) {

                }

                is CurrentAction.Success -> CustomProgress(
                    progressText = "Success",
                    progressValue = "✅",
                    progress = 1f,
                    progressColor = Color(0XFF3690FA),
                    message = viewModel.currentAction.message
                ) {

                }
            }

            if (viewModel.currentAction is CurrentAction.Failed || viewModel.currentAction is CurrentAction.Success) {

                Spacer(modifier = Modifier.height(32.dp))

                CustomButton(
                    text = "Download Another MP3",
                    loadingText = "",
                    loading = false,
                    enabled = true
                ) {
                    viewModel.onResetButtonClick()
                    navHostController.popBackStack()
                }
            }
        }
    }
}