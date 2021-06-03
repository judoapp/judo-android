package app.judo.sdk.api.models

@Suppress("SpellCheckingInspection")
internal enum class NodeType(val code: String) {
    IMAGE("Image"),
    RECTANGLE("Rectangle"),
    WEB("WebView"),
    TEXT("Text"),
    SCREEN("Screen"),
    ZSTACK("ZStack"),
    SPACER("Spacer"),
    VSTACK("VStack"),
    HSTACK("HStack"),
    CAROUSEL("Carousel"),
    COLLECTION("Collection"),
    DATA_SOURCE("DataSource"),
    PAGE_CONTROL("PageControl"),
    SCROLL_CONTAINER("ScrollContainer"),
    AUDIO("Audio"),
    VIDEO("Video"),
    NAMED_ICON("Icon"),
    DIVIDER("Divider");
}