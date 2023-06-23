package com.example.youtubemp3downloader.presentation.common

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.youtubemp3downloader.R
import com.example.youtubemp3downloader.presentation.ui.theme.PlusJakartaSans

@Composable
fun CustomLoading(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            color = Color(0xFF892EFF)
        )
    }
}

@Composable
fun CustomProgress(
    modifier: Modifier = Modifier,
    progressText: String,
    progressValue: String,
    progress: Float,
    progressColor: Color,
    message: String? = null,
    onComplete: () -> Unit
) {

    LaunchedEffect(key1 = progress) {
        if (progress == 1f) {
            onComplete()
        }
    }

    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = progressText,
                fontWeight = FontWeight.Bold,
                fontFamily = PlusJakartaSans,
                color = if (progressText == "Failed") progressColor else Color.Black
            )
            Text(
                text = progressValue,
                fontWeight = FontWeight.Light,
                fontFamily = PlusJakartaSans
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(7.dp)
                .background(
                    color = Color(0XFFEFEFEF)
                )
                .clip(
                    shape = RoundedCornerShape(5.dp)
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress)
                    .background(
                        color = progressColor
                    )
                    .animateContentSize(
                        animationSpec = tween(
                            durationMillis = 500,
                            easing = EaseInOut
                        )
                    )
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        message?.let {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .size(16.dp),
                    imageVector = Icons.Default.Info,
                    contentDescription = stringResource(R.string.text_field_info),
                    tint = Color.Gray,
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    modifier = Modifier,
                    text = it,
                    color = Color.Gray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = PlusJakartaSans,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            }
        }
    }
}