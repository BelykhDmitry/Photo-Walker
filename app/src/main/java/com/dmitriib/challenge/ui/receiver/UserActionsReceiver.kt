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
            (context.applicationContext as ChallengeApplication).appContainer.logger
                .d("Received user Action ${intent.action}")
            // TODO: Pass to record manager
        }
    }
}
