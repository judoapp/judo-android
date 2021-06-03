package app.judo.sdk.api.errors

sealed class ExperienceError : Throwable() {

    data class NetworkError(
        override val message: String? = null,
        override val cause: Throwable? = null
    ): ExperienceError()

    data class MalformedExperienceError(
        override val message: String? = null,
        override val cause: Throwable? = null
    ): ExperienceError()

    data class ExperienceNotFoundError(
        override val message: String? = null,
        override val cause: Throwable? = null
    ): ExperienceError()

    // TODO: 2021-03-23 - Add UnauthorizedAccessError for 401 responses

    data class UnexpectedError(
        override val message: String? = null,
        override val cause: Throwable? = null
    ): ExperienceError()

    data class NotInitialized(
        override val message: String? = null,
        override val cause: Throwable? = null
    ): ExperienceError()

}