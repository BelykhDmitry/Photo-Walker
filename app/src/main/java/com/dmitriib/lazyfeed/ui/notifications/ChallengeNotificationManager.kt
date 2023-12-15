package com.dmitriib.lazyfeed.ui.notifications

import android.Manifest
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE
import androidx.core.app.NotificationManagerCompat
import com.dmitriib.lazyfeed.MainActivity
import com.dmitriib.lazyfeed.ui.receiver.UserActionsReceiver
import com.dmitriib.lazyfeed.ui.services.LocationService
import com.dmitriib.lazyfeed.R

class ChallengeNotificationManager {

    fun initialize(context: Context) {
        val channel = createChannel()
        NotificationManagerCompat
            .from(context)
            .createNotificationChannel(channel)
    }

    fun createNotification(
        context: Context,
        actions: List<NotificationUserAction>,
        currentRecordId: Int
    ): Notification {
        val manager = NotificationManagerCompat
            .from(context)
        val channel = manager.getNotificationChannelCompat(CHANNEL_ID)
            ?: createChannel().apply(manager::createNotificationChannel)

        val notificationBuilder = NotificationCompat.Builder(context, channel.id)
        val intent = Intent(context, MainActivity::class.java).apply {
            putExtras(Bundle().apply { putInt(MainActivity.KEY_FROM_SERVICE, currentRecordId) })
            data = Uri.EMPTY
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            ACTIVITY_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val deleteIntent = PendingIntent.getService(
            context,
            0,
            Intent(context, LocationService::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        notificationBuilder.setOngoing(true)
            .setContentTitle(context.getString(R.string.notification_title))
            .setContentText(context.getString(R.string.notification_message))
            .setPriority(NotificationManagerCompat.IMPORTANCE_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setChannelId(channel.id)
            .setAutoCancel(false)
            .setContentIntent(pendingIntent)
            .setDeleteIntent(deleteIntent)
            .setForegroundServiceBehavior(FOREGROUND_SERVICE_IMMEDIATE)
            .setSmallIcon(R.drawable.directions_walk_24px)
            .setSilent(true)

        actions.forEach { action ->
            val actionIntent = Intent().also {
                action.writeToIntent(it)
                it.setClass(context, UserActionsReceiver::class.java)
            }
            val actionPendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                actionIntent,
                PendingIntent.FLAG_IMMUTABLE
            )
            notificationBuilder.addAction(0, action.getNotificationString(context), actionPendingIntent)
        }

        return notificationBuilder.build()
    }

    fun updateNotification(context: Context, actions: List<NotificationUserAction>, notificationId: Int, currentRecordId: Int) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val notification = createNotification(context, actions, currentRecordId)
            NotificationManagerCompat
                .from(context)
                .notify(notificationId, notification)
        }
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
