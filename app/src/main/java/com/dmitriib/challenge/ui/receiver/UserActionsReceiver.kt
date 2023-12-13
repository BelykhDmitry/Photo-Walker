package com.dmitriib.challenge.ui.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.dmitriib.challenge.ChallengeApplication
import com.dmitriib.challenge.ui.notifications.NotificationUserAction

class UserActionsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val userAction = intent.action?.let(NotificationUserAction::valueOfOrNull)
        userAction?.let {
            val manager = (context.applicationContext as ChallengeApplication)
                .appContainer
                .recordManager
            when (it) {
                NotificationUserAction.START -> manager.startRecord()
                NotificationUserAction.PAUSE -> manager.pauseRecord()
                NotificationUserAction.RESUME -> manager.resumeRecord()
                NotificationUserAction.STOP -> manager.completeRecord()
            }
        }
    }
}
