package app.judo.sdk.utils

import app.judo.sdk.api.logs.LogLevel
import app.judo.sdk.core.log.Logger

class TestLoggerImpl : Logger {

    override var logLevel: LogLevel = LogLevel.Verbose

    override fun d(tag: String?, data: Any?) {
        if (logLevel is LogLevel.Verbose)
            println("$tag: $data")
    }

    override fun v(tag: String?, data: Any?) {
        if (logLevel is LogLevel.Verbose)
            println("$tag: $data")
    }

    override fun e(tag: String?, message: String?, error: Throwable?) {
        if (logLevel is LogLevel.Verbose)
            println("$tag: $message\nERROR: $error")
    }

    override fun i(tag: String?, data: Any?, error: Throwable?) {
        if (logLevel is LogLevel.Verbose)
            println("$tag: $data\nERROR: $error")
    }

}