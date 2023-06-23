package com.example.youtubemp3downloader.presentation.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.youtubemp3downloader.R
import com.example.youtubemp3downloader.presentation.ui.theme.PlusJakartaSans

@Composable
fun CustomVideoCard(
    modifier: Modifier = Modifier,
    videoTitle: String,
    thumbnailUrl: String,
    likes: String,
    views: String
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color(0XFFFAFAFA)
        ),
    ) {

        Column(
            modifier = Modifier
                .padding(vertical = 14.dp, horizontal = 12.dp)
                .fillMaxWidth()
        ) {
            AsyncImage(
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(5.dp)),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(thumbnailUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = stringResource(R.string.thumbnail_image),
            )

            Spacer(
                modifier = Modifier
                    .height(16.dp)
            )

            Text(
                text = videoTitle,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2,
                fontFamily = PlusJakartaSans
            )

            Spacer(
                modifier = Modifier
                    .height(16.dp)
            )

            Row {
                CustomVideoStatsIcon(
                    statsIcon = Icons.Default.RemoveRedEye,
                    statsContentDescription = "",
                    stats = "$views Views"
                )

                Spacer(
                    modifier = Modifier
                        .width(32.dp)
                )

                CustomVideoStatsIcon(
                    statsIcon = Icons.Default.ThumbUp,
                    statsContentDescription = "",
                    stats = "$likes Likes"
                )
            }
        }
    }
}

@Composable
fun CustomVideoStatsIcon(
    modifier: Modifier = Modifier,
    statsIcon: ImageVector,
    statsContentDescription: String,
    stats: String
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier
                .size(16.dp),
            imageVector = statsIcon,
            contentDescription = statsContentDescription,
            tint = Color.Gray
        )

        Spacer(
            modifier = Modifier
                .width(8.dp)
        )

        Text(
            text = stats,
            color = Color.Gray,
            fontWeight = FontWeight.Bold,
            fontFamily = PlusJakartaSans,
            fontSize = 14.sp
        )
    }
}
