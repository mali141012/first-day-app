package com.firstday.habits.reminder

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import com.firstday.habits.MainActivity
import com.firstday.habits.data.db.FirstDayDatabase
import com.firstday.habits.data.entity.toDomain
import com.firstday.habits.domain.model.Habit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

object ReminderScheduler {

    const val CHANNEL_ID = "habit_reminders"
    const val EXTRA_HABIT_ID = "habit_id"
    const val EXTRA_HABIT_NAME = "habit_name"

    fun ensureChannel(context: Context) {
        val mgr = context.getSystemService<NotificationManager>() ?: return
        if (mgr.getNotificationChannel(CHANNEL_ID) != null) return
        val channel = NotificationChannel(
            CHANNEL_ID,
            context.getString(com.firstday.habits.R.string.reminder_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply {
            description = context.getString(com.firstday.habits.R.string.reminder_channel_desc)
        }
        mgr.createNotificationChannel(channel)
    }

    fun schedule(context: Context, habit: Habit) {
        val hour = habit.reminderHour ?: return
        val minute = habit.reminderMinute ?: return
        cancel(context, habit.id)

        val triggerTime = nextTriggerAt(hour, minute)
        setAlarm(context, habit.id, habit.name, triggerTime)
    }

    fun cancel(context: Context, habitId: Long) {
        val am = context.getSystemService<AlarmManager>() ?: return
        val pi = buildPendingIntent(context, habitId, habitName = "")
        am.cancel(pi)
    }

    fun rescheduleAll(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            val db = FirstDayDatabase.get(context)
            db.habitDao().getHabitsWithReminders().forEach { entity ->
                schedule(context, entity.toDomain())
            }
        }
    }

    private fun nextTriggerAt(hour: Int, minute: Int): Long {
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        if (cal.timeInMillis <= System.currentTimeMillis()) {
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        return cal.timeInMillis
    }

    private fun setAlarm(context: Context, habitId: Long, habitName: String, triggerAt: Long) {
        val am = context.getSystemService<AlarmManager>() ?: return
        val pi = buildPendingIntent(context, habitId, habitName)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (am.canScheduleExactAlarms()) {
                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi)
            } else {
                am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi)
            }
        } else {
            am.setExact(AlarmManager.RTC_WAKEUP, triggerAt, pi)
        }
    }

    private fun buildPendingIntent(
        context: Context,
        habitId: Long,
        habitName: String,
    ): PendingIntent {
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra(EXTRA_HABIT_ID, habitId)
            putExtra(EXTRA_HABIT_NAME, habitName)
        }
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        return PendingIntent.getBroadcast(context, habitId.toInt(), intent, flags)
    }
}

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val habitId = intent.getLongExtra(ReminderScheduler.EXTRA_HABIT_ID, -1L)
        val habitName = intent.getStringExtra(ReminderScheduler.EXTRA_HABIT_NAME) ?: ""
        if (habitId == -1L) return

        ReminderScheduler.ensureChannel(context)
        showNotification(context, habitId, habitName)

        CoroutineScope(Dispatchers.IO).launch {
            val db = FirstDayDatabase.get(context)
            val habit = db.habitDao().getById(habitId)?.toDomain()
            if (habit != null && !habit.archived) {
                ReminderScheduler.schedule(context, habit)
            }
        }
    }

    private fun showNotification(context: Context, habitId: Long, habitName: String) {
        val mgr = context.getSystemService<NotificationManager>() ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) return
        }

        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val openPi = PendingIntent.getActivity(
            context, habitId.toInt(), openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val notification = androidx.core.app.NotificationCompat.Builder(context, ReminderScheduler.CHANNEL_ID)
            .setSmallIcon(com.firstday.habits.R.drawable.ic_launcher_foreground)
            .setContentTitle(context.getString(com.firstday.habits.R.string.reminder_title, habitName))
            .setContentText(context.getString(com.firstday.habits.R.string.reminder_body))
            .setPriority(androidx.core.app.NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(openPi)
            .setAutoCancel(true)
            .build()

        mgr.notify(habitId.toInt(), notification)
    }
}
