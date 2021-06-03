package app.judo.sdk.api.models

enum class FontScale(val size: Float) {
    //ios xSmall
    SMALL(0.85f),

    //ios Large
    DEFAULT(1f),

    //ios xLarge
    LARGE(1.15f),

    //ios xxxLarge
    LARGEST(1.3f);

    companion object {
        fun retrieveFontScale(size: Float) = when {
            size < DEFAULT.size -> SMALL
            size < LARGE.size -> DEFAULT
            size < LARGEST.size -> LARGE
            size >= LARGEST.size -> LARGEST
            else -> DEFAULT
        }
    }
}