package com.firstday.habits.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun CompletionCircle(
    completed: Boolean,
    color: Color,
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 56.dp,
    strokeWidth: androidx.compose.ui.unit.Dp = 3.dp,
    onToggle: () -> Unit,
) {
    val ringProgress = remember { Animatable(if (completed) 1f else 0f) }
    val checkScale = remember { Animatable(if (completed) 1f else 0f) }
    val containerScale = remember { Animatable(1f) }

    LaunchedEffect(completed) {
        ringProgress.animateTo(if (completed) 1f else 0f, tween(300))
        if (completed) {
            checkScale.snapTo(0f)
            checkScale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
            containerScale.snapTo(1.15f)
            containerScale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
        }
    }

    Box(
        modifier = modifier
            .size(size)
            .scale(containerScale.value)
            .background(
                if (completed) color.copy(alpha = 0.15f) else Color.Transparent,
                CircleShape,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val stroke = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            drawCircle(
                color = color.copy(alpha = 0.25f),
                style = stroke,
            )
            if (ringProgress.value > 0f) {
                drawArc(
                    color = color,
                    startAngle = -90f,
                    sweepAngle = 360f * ringProgress.value,
                    useCenter = false,
                    style = stroke,
                )
            }
        }
        if (completed) {
            Icon(
                imageVector = Icons.Rounded.Check,
                contentDescription = null,
                tint = color,
                modifier = Modifier
                    .size(size * 0.5f)
                    .scale(checkScale.value),
            )
        }
    }
}
