package app.judo.sdk.core.lang

data class ParserContext<U>(
    val text: String,
    val state: U,
    val position: Int = 0,
    val line: Int = 0
)
