package app.judo.sdk.api.models

enum class ResizingMode(val code: String) {
    SCALE_TO_FIT("scaleToFit"),
    SCALE_TO_FILL("scaleToFill"),
    TILE("tile"),
    STRETCH("stretch"),
    ORIGINAL("originalSize");
}