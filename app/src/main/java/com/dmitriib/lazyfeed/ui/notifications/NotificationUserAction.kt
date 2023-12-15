package com.dmitriib.lazyfeed.ui.notifications

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.dmitriib.lazyfeed.R

sealed class NotificationUserAction(
    private val actionValue: String
) {
    abstract val recordId: Int

    data class Start(override val recordId: Int) : NotificationUserAction(ACTION_START)
    data class Pause(override val recordId: Int) : NotificationUserAction(ACTION_PAUSE)
    data class Resume(override val recordId: Int) : NotificationUserAction(ACTION_RESUME)
    data class Complete(override val recordId: Int) : NotificationUserAction(ACTION_COMPLETE)

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
                ACTION_START -> Start(id)
                ACTION_PAUSE -> Pause(id)
                ACTION_RESUME -> Resume(id)
                ACTION_COMPLETE -> Complete(id)
                else -> null
            }
        }
        private const val ACTION_START = "com.dmitriib.lazyfeed.USER_COMMAND_START"
        private const val ACTION_PAUSE = "com.dmitriib.lazyfeed.USER_COMMAND_PAUSE"
        private const val ACTION_RESUME = "com.dmitriib.lazyfeed.USER_COMMAND_RESUME"
        private const val ACTION_COMPLETE = "com.dmitriib.lazyfeed.USER_COMMAND_COMPLETE"
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
