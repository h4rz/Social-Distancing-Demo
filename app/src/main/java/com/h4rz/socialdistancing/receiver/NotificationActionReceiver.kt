package com.h4rz.socialdistancing.receiver

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.util.Log
import com.h4rz.socialdistancing.notification.NotificationManager
import com.h4rz.socialdistancing.utility.Constants
import com.h4rz.socialdistancing.utility.SnoozeUtils


/**
 * Created by Darshan Parikh on 04/05/20.
 */

class NotificationActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if(context == null) return
        if(intent == null) return
        if(intent.action != Constants.ACTION_NOTIFICATION_SNOOZE) return

        val notificationSnoozeTime = intent.getLongExtra(Constants.NOTIFICATION_SNOOZE_TIME, 0L)
        Log.i(TAG, "Notification snooze for $notificationSnoozeTime milliseconds")
        if (notificationSnoozeTime == 0L) return

        // Disable notifications
        NotificationManager().removeWarningNotifications(context)
        SnoozeUtils.setShowNotification(context, false)

        // Set alarm manager to enable notifications after certain time
        val pendingIntent = getButtonIntent(context)
        val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager
        val notifyTime = System.currentTimeMillis() + notificationSnoozeTime
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, notifyTime, pendingIntent)
    }

    private fun getButtonIntent(context: Context): PendingIntent {
        val buttonIntent = Intent(context, SnoozeTimeOverReceiver::class.java)
        buttonIntent.action = Constants.ACTION_SNOOZE_TIME_OVER
        return PendingIntent.getBroadcast(context, 0, buttonIntent, 0)
    }

    companion object {
        val TAG = NotificationActionReceiver::class.java.simpleName
    }
}