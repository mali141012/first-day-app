package com.firstday.habits.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.firstday.habits.domain.model.DayCompletion
import com.firstday.habits.domain.util.DateUtils

@Composable
fun BarChart(
    data: List<DayCompletion>,
    completedColor: Color,
    missedColor: Color,
    modifier: Modifier = Modifier,
    showDayLabels: Boolean = true,
) {
    if (data.isEmpty()) return

    val maxValue = data.size
    val barSpacing = 4.dp
    val labelHeight = if (showDayLabels) 18.dp else 0.dp

    Column(modifier = modifier.fillMaxWidth()) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp),
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height - labelHeight.toPx()
            val totalSpacing = barSpacing.toPx() * (data.size - 1)
            val barWidth = (canvasWidth - totalSpacing) / data.size

            data.forEachIndexed { index, day ->
                val x = index * (barWidth + barSpacing.toPx())
                val isCompleted = day.completed && day.scheduled
                val isMissed = !day.completed && day.scheduled
                val barHeight = if (isCompleted) canvasHeight else if (isMissed) canvasHeight * 0.4f else 0f
                val color = when {
                    isCompleted -> completedColor
                    isMissed -> missedColor
                    else -> Color.Transparent
                }
                drawRoundRect(
                    color = color,
                    topLeft = Offset(x, canvasHeight - barHeight),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(barWidth / 4, barWidth / 4),
                )
            }
        }

        if (showDayLabels) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                data.forEachIndexed { index, day ->
                    Text(
                        text = DateUtils.dayOfWeekShort(day.date, startOnSunday = false),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}
