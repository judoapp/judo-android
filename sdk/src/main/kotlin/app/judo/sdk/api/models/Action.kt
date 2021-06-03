package app.judo.sdk.api.models

import com.squareup.moshi.JsonClass

sealed class Action {

    @JsonClass(generateAdapter = true)
    class Close : Action() {
        override fun equals(other: Any?): Boolean {
            return other is Close
        }

        override fun hashCode(): Int {
            return "close-action-123".hashCode()
        }
    }

    @JsonClass(generateAdapter = true)
    data class PerformSegue(
        val screenID: String,
        val segueStyle: SegueStyle
    ) : Action()

    @JsonClass(generateAdapter = true)
    data class OpenURL(
        val url: String,
        val dismissExperience: Boolean
    ) : Action()

    @JsonClass(generateAdapter = true)
    data class PresentWebsite(
        val url: String,
    ) : Action()

    @JsonClass(generateAdapter = true)
    data class Custom(
        val dismissExperience: Boolean,
    ) : Action()
}

