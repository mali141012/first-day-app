package com.firstday.habits.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.firstday.habits.ui.theme.ForestGreen
import com.firstday.habits.ui.theme.ForestGreenLight
import com.firstday.habits.ui.theme.CoralAccent
import com.firstday.habits.ui.theme.CoralAccentLight

@Composable
fun StatsEmptyIllustration(
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 160.dp,
) {
    val transition = rememberInfiniteTransition(label = "stats_empty")
    val grow by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "grow",
    )

    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height
            val baseY = h * 0.8f

            val bars = listOf(
                Triple(w * 0.2f, ForestGreenLight, 0.5f),
                Triple(w * 0.35f, CoralAccent, 0.7f),
                Triple(w * 0.5f, ForestGreen, 0.9f),
                Triple(w * 0.65f, CoralAccentLight, 0.6f),
                Triple(w * 0.8f, ForestGreenLight, 0.4f),
            )

            bars.forEach { (x, color, baseHeight) ->
                val barH = h * 0.5f * baseHeight * grow
                drawRoundRect(
                    color = color.copy(alpha = 0.7f),
                    topLeft = Offset(x - 12f, baseY - barH),
                    size = Size(24f, barH),
                    cornerRadius = CornerRadius(8f, 8f),
                )
            }

            drawLine(
                color = Color.Gray.copy(alpha = 0.2f),
                start = Offset(w * 0.1f, baseY),
                end = Offset(w * 0.9f, baseY),
                strokeWidth = 2f,
            )
        }
    }
}
