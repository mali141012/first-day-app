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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.firstday.habits.ui.theme.ForestGreen
import com.firstday.habits.ui.theme.ForestGreenLight
import com.firstday.habits.ui.theme.CoralAccent

@Composable
fun EmptyStateIllustration(
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 160.dp,
) {
    val transition = rememberInfiniteTransition(label = "empty_state")
    val float by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "float",
    )

    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height
            val center = Offset(w / 2, h / 2)
            val bob = float * 6f

            // Ground line
            drawLine(
                color = ForestGreenLight.copy(alpha = 0.4f),
                start = Offset(w * 0.15f, h * 0.82f),
                end = Offset(w * 0.85f, h * 0.82f),
                strokeWidth = 3f,
                cap = StrokeCap.Round,
            )

            // Sprout pot
            val potLeft = w * 0.38f
            val potRight = w * 0.62f
            val potTop = h * 0.58f
            val potBottom = h * 0.82f
            val potPath = Path().apply {
                moveTo(potLeft, potTop)
                lineTo(potRight, potTop)
                lineTo(potRight - 8f, potBottom)
                lineTo(potLeft + 8f, potBottom)
                close()
            }
            drawPath(potPath, color = CoralAccent.copy(alpha = 0.8f))

            // Stem
            val stemBase = Offset(center.x, potTop)
            val stemTop = Offset(center.x, h * 0.3f - bob)
            drawLine(
                color = ForestGreen,
                start = stemBase,
                end = stemTop,
                strokeWidth = 4f,
                cap = StrokeCap.Round,
            )

            // Left leaf
            val leafPath = Path().apply {
                moveTo(stemTop.x, stemTop.y + 10f)
                cubicTo(
                    stemTop.x - 30f, stemTop.y - 5f,
                    stemTop.x - 35f, stemTop.y + 20f,
                    stemTop.x - 5f, stemTop.y + 25f,
                )
                close()
            }
            drawPath(leafPath, color = ForestGreenLight)

            // Right leaf
            val leafPath2 = Path().apply {
                moveTo(stemTop.x, stemTop.y + 25f)
                cubicTo(
                    stemTop.x + 28f, stemTop.y + 10f,
                    stemTop.x + 33f, stemTop.y + 35f,
                    stemTop.x + 5f, stemTop.y + 38f,
                )
                close()
            }
            drawPath(leafPath2, color = ForestGreen)

            // Small check bubble floating
            val bubbleY = h * 0.18f - bob * 1.5f
            drawCircle(
                color = ForestGreen.copy(alpha = 0.15f),
                radius = 14f,
                center = Offset(w * 0.72f, bubbleY),
            )
            // Checkmark
            val checkPath = Path().apply {
                moveTo(w * 0.72f - 5f, bubbleY)
                lineTo(w * 0.72f - 1f, bubbleY + 4f)
                lineTo(w * 0.72f + 6f, bubbleY - 5f)
            }
            drawPath(
                checkPath,
                color = ForestGreen,
                style = Stroke(width = 3f, cap = StrokeCap.Round),
            )
        }
    }
}
