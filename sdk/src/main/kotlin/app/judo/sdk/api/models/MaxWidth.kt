package app.judo.sdk.api.models

sealed class MaxWidth {

    data class Finite(
        val value: Float
    ) : MaxWidth()

    @Suppress("CanSealedSubClassBeObject")
    class Infinite : MaxWidth()

}