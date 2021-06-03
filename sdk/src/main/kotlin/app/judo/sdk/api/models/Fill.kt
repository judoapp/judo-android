package app.judo.sdk.api.models

sealed class Fill {

    data class FlatFill(
        val color: ColorVariants
    ) : Fill()

    data class GradientFill(
        val gradient: GradientVariants
    ) : Fill()
}