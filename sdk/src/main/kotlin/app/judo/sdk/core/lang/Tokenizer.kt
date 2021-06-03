package app.judo.sdk.core.lang

internal fun interface Tokenizer {

    fun tokenize(text: String): List<Token>

}