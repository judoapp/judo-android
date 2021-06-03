package app.judo.sdk.api.models

sealed class MaxHeight {

    data class Finite(
        val value: Float
    ) : MaxHeight()

    @Suppress("CanSealedSubClassBeObject")
    class Infinite : MaxHeight()

}