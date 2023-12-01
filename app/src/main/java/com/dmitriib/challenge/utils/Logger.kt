package com.dmitriib.challenge.utils

import android.util.Log

interface Logger {
    fun d(message: String, throwable: Throwable? = null)
}

class ConsoleLogger : Logger {

    override fun d(message: String, throwable: Throwable?) {
        throwable?.let {
            Log.d(LOG_TAG, message, it)
        } ?: Log.d(LOG_TAG, message)
    }

    companion object {
        private const val LOG_TAG = "ChallengeAppLog"
    }
}
