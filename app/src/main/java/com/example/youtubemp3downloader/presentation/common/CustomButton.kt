package com.example.youtubemp3downloader.presentation.common

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.youtubemp3downloader.presentation.ui.theme.PlusJakartaSans

@Composable
fun CustomButton(
    modifier: Modifier = Modifier,
    text: String,
    loadingText: String,
    loading: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {

    val backgroundColor = animateColorAsState(
        targetValue = if (loading) Color(0xFF9D7CC8) else Color(0xFF892EFF)
    )

    Button(
        modifier = modifier
            .fillMaxWidth(),
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor.value,
            contentColor = Color.White,
            disabledContainerColor = Color(0xFF9D7CC8)
        ),
        shape = RoundedCornerShape(
            10.dp
        ),
        contentPadding = PaddingValues(
            vertical = 20.dp
        ),
        enabled = enabled
    ) {
        if (loading) {
            CircularProgressIndicator(
                color = Color.White,
                strokeWidth = 1.dp,
                modifier = Modifier
                    .size(24.dp)
            )

            Spacer(
                modifier = Modifier
                    .width(16.dp)
            )

            Text(
                text = loadingText,
                fontWeight = FontWeight.Bold,
                fontFamily = PlusJakartaSans,
                fontSize = 16.sp,
                color = Color.White
            )

        } else {
            Text(
                text = text,
                fontWeight = FontWeight.Bold,
                fontFamily = PlusJakartaSans,
                fontSize = 16.sp,
                color = Color.White
            )
        }
    }
}
