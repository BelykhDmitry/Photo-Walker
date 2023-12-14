package com.dmitriib.challenge.ui.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.dmitriib.challenge.ChallengeApplication
import com.dmitriib.challenge.ui.notifications.NotificationUserAction
import com.dmitriib.challenge.ui.notifications.NotificationUserAction.Companion.readUserAction

class UserActionsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val userAction = intent.readUserAction()
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
