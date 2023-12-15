package com.dmitriib.lazyfeed.ui.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.dmitriib.lazyfeed.ChallengeApplication
import com.dmitriib.lazyfeed.ui.notifications.NotificationUserAction
import com.dmitriib.lazyfeed.ui.notifications.NotificationUserAction.Companion.readUserAction
import com.dmitriib.lazyfeed.utils.ConsoleLogger

class UserActionsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val userAction = intent.readUserAction()
        ConsoleLogger().d("Received $userAction from $intent")
        userAction?.let {
            val manager = (context.applicationContext as ChallengeApplication)
                .appContainer
                .recordManagerFactory
                .create(it.recordId)
            when (it) {
                is NotificationUserAction.Start -> manager.startRecord()
                is NotificationUserAction.Pause -> manager.pauseRecord()
                is NotificationUserAction.Resume -> manager.resumeRecord()
                is NotificationUserAction.Complete -> manager.completeRecord()
            }
        }
    }
}
