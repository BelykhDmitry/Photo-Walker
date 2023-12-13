package com.dmitriib.challenge.ui.notifications

import android.content.Context
import com.dmitriib.dmitrii_belykh_challenge.R

enum class NotificationUserAction(
    val actionValue: String
) {
    START("UserCommandStart"),
    PAUSE("UserCommandPause"),
    RESUME("UserCommandResume"),
    COMPLETE("UserCommandComplete"),
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
        NotificationUserAction.COMPLETE -> R.string.complete
        NotificationUserAction.START -> R.string.start
    })
}
