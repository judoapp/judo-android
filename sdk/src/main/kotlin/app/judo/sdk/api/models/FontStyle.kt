package app.judo.sdk.api.models

@Suppress("SpellCheckingInspection")
enum class FontStyle(val code: String) {
    LARGE_TITLE("largeTitle"),
    TITLE_1("title"),
    TITLE_2("title2"),
    TITLE_3("title3"),
    HEADLINE("headline"),
    BODY("body"),
    CALLOUT("callout"),
    SUBHEADLINE("subheadline"),
    FOOTNOTE("footnote"),
    CAPTION_1("caption"),
    CAPTION_2("caption2");

    companion object {
        fun getStyleFromCode(code: String): FontStyle {
            return values().find { it.code == code } ?: throw RuntimeException("Incorrect font style code ${code}")
        }
    }
}