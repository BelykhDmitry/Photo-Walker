package com.dmitriib.challenge.ui.notifications

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.dmitriib.dmitrii_belykh_challenge.R

sealed class NotificationUserAction(
    val actionValue: String
) {
    abstract val recordId: Int

    data class Start(override val recordId: Int) : NotificationUserAction("UserCommandStart")
    data class Pause(override val recordId: Int) : NotificationUserAction("UserCommandPause")
    data class Resume(override val recordId: Int) : NotificationUserAction("UserCommandResume")
    data class Complete(override val recordId: Int) : NotificationUserAction("UserCommandComplete")

    fun writeToIntent(intent: Intent) = with(intent) {
        action = actionValue
        putExtras(Bundle().also { putExtra(RECORD_ID_KEY, recordId) })
        data = Uri.EMPTY
    }

    companion object {
        private const val RECORD_ID_KEY = "record_id_key"
        fun Intent.readUserAction(): NotificationUserAction? {
            val id = getIntExtra(RECORD_ID_KEY, -1)
            return when (action) {
                "UserCommandStart" -> Start(id)
                "UserCommandPause" -> Pause(id)
                "UserCommandResume" -> Resume(id)
                "UserCommandComplete" -> Complete(id)
                else -> null
            }
        }
    }
}

// TODO: move from here
fun NotificationUserAction.getNotificationString(context: Context): String {
    return context.getString(when (this) {
        is NotificationUserAction.Pause -> R.string.pause
        is NotificationUserAction.Resume -> R.string.resume
        is NotificationUserAction.Complete -> R.string.complete
        is NotificationUserAction.Start -> R.string.start
    })
}
