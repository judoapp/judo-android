package app.judo.sdk.api.logs

sealed class LogLevel {
    /**
     * Shows only Error and Warning logs
     */
    object Error: LogLevel()

    /**
     * Shows all the logs
     *
     * _Warning_: this can get very noisy.
     */
    object Verbose: LogLevel()
}