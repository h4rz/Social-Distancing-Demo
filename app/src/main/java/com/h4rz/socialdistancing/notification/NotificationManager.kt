package com.h4rz.socialdistancing.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.h4rz.socialdistancing.R
import com.h4rz.socialdistancing.activities.MainActivity
import com.h4rz.socialdistancing.receiver.NotificationActionReceiver
import com.h4rz.socialdistancing.utility.Constants
import com.h4rz.socialdistancing.utility.Constants.WARNING_NOTIFICATION_ID
import com.h4rz.socialdistancing.utility.SnoozeUtils


/**
 * Created by Harsh Rajgor on 2020-02-13.
 */

class NotificationManager {

    companion object {
        const val CHANNEL_GENERAL = "general"
    }

    private lateinit var context: Context

    fun buildNotification(
        context: Context,
        title: String,
        message: String
    ) {
        val builder = getNotificationBuilder(context, title, message, true)
        //clear previous notifications
        removeWarningNotifications(context)
        // Show notification
        showNotification(context, builder)
    }

    fun getNotificationBuilder(
        context: Context,
        title: String,
        message: String,
        isWarningNotification: Boolean = false
    ): NotificationCompat.Builder {
        this.context = context
        createNotificationChannel(context)

        val pendingIntent = getPendingIntent()

        val builder = NotificationCompat.Builder(context, CHANNEL_GENERAL)
            .setContentTitle(title)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentText(message)
            .setContentIntent(pendingIntent)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        if(isWarningNotification) {
            val buttonIntent = getButtonIntent(context)
            builder.addAction(0, context.getString(R.string.snooze_one_hour), buttonIntent)
        }
        return builder
    }

    private fun getButtonIntent(context: Context): PendingIntent {
        val buttonIntent = Intent(context, NotificationActionReceiver::class.java)
        buttonIntent.action = Constants.ACTION_NOTIFICATION_SNOOZE
        buttonIntent.putExtra(Constants.NOTIFICATION_SNOOZE_TIME, Constants.ONE_HOUR)
        return PendingIntent.getBroadcast(context, 0, buttonIntent, 0)
    }

    private fun getPendingIntent(): PendingIntent {
        val resultIntent = Intent(context, MainActivity::class.java)
        resultIntent.flags =
            (Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        return TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(resultIntent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }

    private fun showNotification(
        context: Context,
        builder: NotificationCompat.Builder
    ) {
        if(!SnoozeUtils.isShowNotification(context)) return

        with(NotificationManagerCompat.from(context)) {
            notify(WARNING_NOTIFICATION_ID, builder.build())
        }
    }

    fun removeWarningNotifications(context: Context) {
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(WARNING_NOTIFICATION_ID)
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_GENERAL, CHANNEL_GENERAL, importance).apply {
                description = CHANNEL_GENERAL
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

}