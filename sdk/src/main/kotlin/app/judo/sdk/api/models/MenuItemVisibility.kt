package app.judo.sdk.api.models

enum class MenuItemVisibility(val code: String) {
    ALWAYS("always"),
    NEVER("never"),
    IF_ROOM("ifRoom");
}