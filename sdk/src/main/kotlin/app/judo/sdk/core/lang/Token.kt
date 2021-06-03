package app.judo.sdk.core.lang

internal sealed class Token {

    abstract val value: String

    abstract val position: Int

    data class RegularText(
        override val value: String,
        override val position: Int = 0,
    ) : Token()

    data class HandleBarExpression(
        override val value: String,
        val keys: List<String> = emptyList(),
        val keyword: Keyword = Keyword.DATA,
        val functionName: FunctionName? = null,
        val functionArgument: String? = null,
        override val position: Int = 0
    ) : Token()
}