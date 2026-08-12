package com.firstday.habits.ui.components
import androidx.compose.foundation.background
import android.os.Build
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.firstday.habits.domain.util.DateUtils
import com.firstday.habits.ui.theme.HeatmapEmpty
import com.firstday.habits.ui.theme.HeatmapEmptyDark
import com.firstday.habits.ui.theme.HeatmapLevel1
import com.firstday.habits.ui.theme.HeatmapLevel2
import com.firstday.habits.ui.theme.HeatmapLevel3
import com.firstday.habits.ui.theme.HeatmapLevel4
import java.time.LocalDate

@Composable
fun CalendarHeatmap(
    completions: Set<LocalDate>,
    startDate: LocalDate,
    endDate: LocalDate,
    habitColor: Color,
    startOnSunday: Boolean,
    modifier: Modifier = Modifier,
) {
    val weeks = DateUtils.weeksInYearRange(startDate, endDate, startOnSunday)
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            listOf("Jan", "Apr", "Jul", "Oct").forEach {
                Text(
                    text = it,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Start,
                )
            }
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            items(weeks) { weekStart ->
                WeekColumn(
                    weekStart = weekStart,
                    completions = completions,
                    habitColor = habitColor,
                    isDark = isDark,
                )
            }
        }

        LegendRow(habitColor, isDark)
    }
}

@Composable
private fun WeekColumn(
    weekStart: LocalDate,
    completions: Set<LocalDate>,
    habitColor: Color,
    isDark: Boolean,
) {
    val days = (0..6).map { weekStart.plusDays(it.toLong()) }
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        days.forEach { date ->
            val filled = completions.contains(date)
            val isFuture = date.isAfter(LocalDate.now())
            val color = when {
                isFuture -> Color.Transparent
                filled -> habitColor
                else -> if (isDark) HeatmapEmptyDark else HeatmapEmpty
            }
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(color),
            )
        }
    }
}

@Composable
private fun LegendRow(habitColor: Color, isDark: Boolean) {
    val ramp = listOf(
        if (isDark) HeatmapEmptyDark else HeatmapEmpty,
        HeatmapLevel1,
        HeatmapLevel2,
        HeatmapLevel3,
        HeatmapLevel4,
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            "Less",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.width(6.dp))
        ramp.forEach { c ->
            Box(
                modifier = Modifier
                    .padding(horizontal = 1.dp)
                    .size(10.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(if (c == habitColor) habitColor else c),
            )
        }
        Spacer(Modifier.width(6.dp))
        Text(
            "More",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

private fun Color.luminance(): Float {
    val r = red * 0.299f
    val g = green * 0.587f
    val b = blue * 0.114f
    return r + g + b
}
