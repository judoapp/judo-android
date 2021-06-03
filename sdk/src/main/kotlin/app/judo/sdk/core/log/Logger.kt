package app.judo.sdk.core.log

import app.judo.sdk.api.logs.LogLevel

interface Logger {

    var logLevel: LogLevel

    fun i(tag: String? = null, data: Any? = null, error: Throwable? = null)
    fun d(tag: String? = null, data: Any? = null)
    fun e(tag: String? = null, message: String? = null, error: Throwable? = null)
    fun v(tag: String? = null, data: Any? = null)
}
