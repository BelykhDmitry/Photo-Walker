package com.dmitriib.challenge.ui.notifications

import android.content.Context
import com.dmitriib.dmitrii_belykh_challenge.R

enum class NotificationUserAction(
    val actionValue: String
) {
    PAUSE("UserCommandPause"),
    RESUME("UserCommandResume"),
    STOP("UserCommandStop"),
    ;

    companion object {
        fun valueOfOrNull(action: String) = values().find { it.actionValue == action }
    }
}

// TODO: move from here
fun NotificationUserAction.getNotificationString(context: Context): String {
    return context.getString(when (this) {
        NotificationUserAction.PAUSE -> R.string.pause
        NotificationUserAction.RESUME -> R.string.resume
        NotificationUserAction.STOP -> R.string.stop
    })
}
