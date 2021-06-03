package app.judo.sdk.api.models

import com.squareup.moshi.JsonClass

@Suppress("CanSealedSubClassBeObject")
sealed class PageControlStyle {

    @JsonClass(generateAdapter = true)
    class DefaultPageControlStyle : PageControlStyle()

    @JsonClass(generateAdapter = true)
    class LightPageControlStyle : PageControlStyle()

    @JsonClass(generateAdapter = true)
    class DarkPageControlStyle : PageControlStyle()

    @JsonClass(generateAdapter = true)
    class InvertedPageControlStyle : PageControlStyle()

    @JsonClass(generateAdapter = true)
    data class CustomPageControlStyle(
        val normalColor: ColorVariants,
        val currentColor: ColorVariants
    ) : PageControlStyle()

    @JsonClass(generateAdapter = true)
    data class ImagePageControlStyle(
        val normalColor: ColorVariants,
        val currentColor: ColorVariants,
        val normalImage: Image,
        val currentImage: Image
    ) : PageControlStyle()
}