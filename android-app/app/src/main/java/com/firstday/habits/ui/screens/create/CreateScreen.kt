package com.firstday.habits.ui.screens.create

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.firstday.habits.R
import com.firstday.habits.domain.model.FrequencyType

private val EMOJI_CHOICES = listOf(
    "🌱", "📚", "💧", "🏃", "🧘", "💪", "🎯", "☀️",
    "🛌", "🥗", "🧹", "✍️", "🎨", "🎸", "💻", "🚭",
)

private val COLOR_CHOICES = listOf(
    "#2D6A4F", "#E76F51", "#264653", "#E9C46A",
    "#F4A261", "#1B4332", "#BC4749", "#6A4C93",
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CreateScreen(
    viewModel: CreateViewModel,
    onBack: () -> Unit,
    contentPadding: PaddingValues,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showTimePicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(
                            if (state.isEdit) R.string.create_title_edit else R.string.create_title_new
                        ),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null)
                    }
                },
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(contentPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            NameSection(state, viewModel::updateName)
            EmojiSection(state.emoji, viewModel::updateEmoji)
            ColorSection(state.colorHex, viewModel::updateColor)
            FrequencySection(state, viewModel)
            ReminderSection(state, viewModel, onPickTime = { showTimePicker = true })

            Button(
                onClick = { viewModel.save(onBack) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
            ) {
                Text(
                    text = stringResource(R.string.create_save),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 4.dp),
                )
            }

            Spacer(Modifier.height(80.dp))
        }
    }

    if (showTimePicker) {
        val tpState = rememberTimePickerState(
            initialHour = state.reminderHour,
            initialMinute = state.reminderMinute,
            is24Hour = false,
        )
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                Button(onClick = {
                    viewModel.updateReminderTime(tpState.hour, tpState.minute)
                    showTimePicker = false
                }) { Text(stringResource(R.string.action_done)) }
            },
            text = { TimePicker(state = tpState) },
        )
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontWeight = FontWeight.Medium,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NameSection(state: CreateUiState, onNameChange: (String) -> Unit) {
    Column {
        SectionLabel(stringResource(R.string.create_name_label))
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = state.name,
            onValueChange = onNameChange,
            placeholder = { Text(stringResource(R.string.create_name_placeholder)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Done,
            ),
            shape = RoundedCornerShape(14.dp),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun EmojiSection(selected: String, onPick: (String) -> Unit) {
    Column {
        SectionLabel(stringResource(R.string.create_icon_label))
        Spacer(Modifier.height(8.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            EMOJI_CHOICES.forEach { emoji ->
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (emoji == selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        )
                        .clickable { onPick(emoji) },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(text = emoji, style = MaterialTheme.typography.headlineMedium)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun ColorSection(selected: String, onPick: (String) -> Unit) {
    Column {
        SectionLabel(stringResource(R.string.create_color_label))
        Spacer(Modifier.height(8.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            COLOR_CHOICES.forEach { hex ->
                val color = Color(android.graphics.Color.parseColor(hex))
                val isSelected = hex == selected
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(color)
                        .then(
                            if (isSelected) Modifier.border(3.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                            else Modifier
                        )
                        .clickable { onPick(hex) },
                    contentAlignment = Alignment.Center,
                ) {
                    if (isSelected) {
                        Icon(
                            Icons.Rounded.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FrequencySection(state: CreateUiState, viewModel: CreateViewModel) {
    Column {
        SectionLabel(stringResource(R.string.create_frequency_label))
        Spacer(Modifier.height(8.dp))

        val options = listOf(
            stringResource(R.string.freq_daily) to FrequencyType.DAILY,
            stringResource(R.string.freq_weekdays) to FrequencyType.SPECIFIC_DAYS,
            stringResource(R.string.freq_x_week) to FrequencyType.X_TIMES_WEEK,
        )

        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            options.forEachIndexed { index, (label, type) ->
                SegmentedButton(
                    selected = state.frequencyType == type,
                    onClick = { viewModel.updateFrequencyType(type) },
                    shape = SegmentedButtonDefaults.itemShape(index, options.size),
                ) { Text(label, style = MaterialTheme.typography.labelLarge) }
            }
        }

        Spacer(Modifier.height(12.dp))

        when (state.frequencyType) {
            FrequencyType.SPECIFIC_DAYS -> WeekdayPicker(state, viewModel)
            FrequencyType.X_TIMES_WEEK -> TimesPerWeekPicker(state, viewModel)
            FrequencyType.DAILY -> { /* no extra UI needed */ }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun WeekdayPicker(state: CreateUiState, viewModel: CreateViewModel) {
    val dayLabels = listOf("S", "M", "T", "W", "T", "F", "S")
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        (0..6).forEach { day ->
            FilterChip(
                selected = state.selectedWeekdays.contains(day),
                onClick = { viewModel.toggleWeekday(day) },
                label = { Text(dayLabels[day]) },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimesPerWeekPicker(state: CreateUiState, viewModel: CreateViewModel) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        (1..7).forEach { n ->
            FilterChip(
                selected = state.timesPerWeek == n,
                onClick = { viewModel.updateTimesPerWeek(n) },
                label = { Text(n.toString()) },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReminderSection(
    state: CreateUiState,
    viewModel: CreateViewModel,
    onPickTime: () -> Unit,
) {
    Column {
        SectionLabel(stringResource(R.string.create_reminder_label))
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (state.reminderEnabled) {
                        val amPm = if (state.reminderHour < 12) "AM" else "PM"
                        val hour12 = if (state.reminderHour == 0) 12 else if (state.reminderHour > 12) state.reminderHour - 12 else state.reminderHour
                        String.format("%d:%02d %s", hour12, state.reminderMinute, amPm)
                    } else {
                        stringResource(R.string.create_reminder_off)
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (state.reminderEnabled) MaterialTheme.colorScheme.onSurface
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (state.reminderEnabled) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Tap to change time",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable { onPickTime() },
                    )
                }
            }
            Switch(
                checked = state.reminderEnabled,
                onCheckedChange = { viewModel.updateReminderEnabled(it) },
            )
        }
    }
}
