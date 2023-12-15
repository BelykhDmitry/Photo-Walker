package com.dmitriib.lazyfeed.fake

import com.dmitriib.lazyfeed.utils.Logger

class FakeLogger : Logger {
    override fun d(message: String, throwable: Throwable?) = Unit
}