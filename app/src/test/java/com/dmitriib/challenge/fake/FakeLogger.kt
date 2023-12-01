package com.dmitriib.challenge.fake

import com.dmitriib.challenge.utils.Logger

class FakeLogger : Logger {
    override fun d(message: String, throwable: Throwable?) = Unit
}