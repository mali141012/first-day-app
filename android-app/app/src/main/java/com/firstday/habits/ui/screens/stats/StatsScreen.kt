package com.firstday.habits.ui.screens.stats

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.firstday.habits.R
import com.firstday.habits.ui.components.BarChart
import com.firstday.habits.ui.components.StatCard
import com.firstday.habits.ui.components.StatsEmptyIllustration
import com.firstday.habits.ui.theme.CoralAccent
import com.firstday.habits.ui.theme.ForestGreen

@Composable
fun StatsScreen(
    viewModel: StatsViewModel,
    contentPadding: PaddingValues,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    if (!state.hasData && !state.loading) {
        EmptyStats(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
        )
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(contentPadding)
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Text(
            text = stringResource(R.string.stats_title),
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            StatCard(
                label = stringResource(R.string.stats_total_completions),
                value = state.totalCompletions.toString(),
                accentColor = ForestGreen,
                modifier = Modifier.weight(1f),
            )
            StatCard(
                label = stringResource(R.string.stats_best_streak),
                value = state.bestStreak.toString(),
                accentColor = CoralAccent,
                modifier = Modifier.weight(1f),
            )
        }

        ChartCard(
            title = stringResource(R.string.stats_weekly),
            data = state.weeklyData,
            completedColor = ForestGreen,
            missedColor = CoralAccent.copy(alpha = 0.4f),
            showDayLabels = true,
        )

        ChartCard(
            title = stringResource(R.string.stats_monthly),
            data = state.monthlyData,
            completedColor = ForestGreen,
            missedColor = CoralAccent.copy(alpha = 0.4f),
            showDayLabels = false,
        )

        Spacer(Modifier.height(80.dp))
    }
}

@Composable
private fun ChartCard(
    title: String,
    data: List<com.firstday.habits.domain.model.DayCompletion>,
    completedColor: Color,
    missedColor: Color,
    showDayLabels: Boolean,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(16.dp))
            BarChart(
                data = data,
                completedColor = completedColor,
                missedColor = missedColor,
                showDayLabels = showDayLabels,
            )
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                LegendDot(completedColor, stringResource(R.string.stats_completed))
                Spacer(Modifier.width(16.dp))
                LegendDot(missedColor, stringResource(R.string.stats_missed))
            }
        }
    }
}

@Composable
private fun LegendDot(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Canvas(modifier = Modifier.size(10.dp)) {
            drawCircle(color)
        }
        Spacer(Modifier.width(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun EmptyStats(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        StatsEmptyIllustration()
        Spacer(Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.stats_empty_title),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.stats_empty_body),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}
