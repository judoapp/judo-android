package app.judo.sdk.core.implementations

import android.util.Log
import app.judo.sdk.api.logs.LogLevel
import app.judo.sdk.core.log.Logger

class ProductionLoggerImpl : Logger {

    override var logLevel: LogLevel = LogLevel.Error

    override fun d(tag: String?, data: Any?) {
        if (logLevel is LogLevel.Verbose)
            Log.d(tag, data.toString())
    }

    override fun v(tag: String?, data: Any?) {
        if (logLevel is LogLevel.Verbose)
            Log.v(tag, data.toString())
    }

    override fun e(tag: String?, message: String?, error: Throwable?) {
        Log.e(tag, message, error)
    }

    override fun i(tag: String?, data: Any?, error: Throwable?) {
        if (logLevel is LogLevel.Verbose)
            Log.i(tag, data.toString(), error)
    }

}