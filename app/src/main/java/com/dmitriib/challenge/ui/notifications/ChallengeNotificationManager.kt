package com.dmitriib.challenge.ui.notifications

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.dmitriib.challenge.MainActivity
import com.dmitriib.dmitrii_belykh_challenge.R

class ChallengeNotificationManager {

    fun initialize(context: Context) {
        val channel = createChannel()
        NotificationManagerCompat
            .from(context)
            .createNotificationChannel(channel)
    }

    fun createNotification(context: Context): Notification {
        val manager = NotificationManagerCompat
            .from(context)
        val channel = manager.getNotificationChannelCompat(CHANNEL_ID)
            ?: createChannel().apply(manager::createNotificationChannel)

        val notificationBuilder = NotificationCompat.Builder(context, channel.id)
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            ACTIVITY_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        return notificationBuilder.setOngoing(true)
            .setContentTitle(context.getString(R.string.notification_title))
            .setContentText(context.getString(R.string.notification_message))
            .setPriority(NotificationManagerCompat.IMPORTANCE_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setChannelId(channel.id)
            .setAutoCancel(false)
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.directions_walk_24px)
            .build()
    }

    private fun createChannel(): NotificationChannelCompat {
        return NotificationChannelCompat
            .Builder(CHANNEL_ID, NotificationManagerCompat.IMPORTANCE_DEFAULT)
            .setName(CHANNEL_NAME)
            .build()
    }

    companion object {
        private const val CHANNEL_ID = "ChallengeChannelId"
        private const val CHANNEL_NAME = "Challenge Location Service"
        private const val ACTIVITY_REQUEST_CODE = 100
    }
}
