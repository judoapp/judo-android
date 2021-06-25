package app.judo.sdk.core.extensions

import app.judo.sdk.core.lang.*
import java.net.URI

internal fun String.urlParams(): Map<String, String> {

    val query = URI.create(this)?.query ?: ""

    val identifier = IdentifierParser<Unit>('=', '&')

    val keyValueParser = AndParser(
        identifier,
        AndRightParser(
            CharParser('='),
            identifier
        )
    )

    val parser = ManyParser(OrParser(keyValueParser, AndRightParser(CharParser('&'), keyValueParser)))

    val context = ParserContext(query, Unit)

    return when (val result = parser.parse(context)) {
        is Parser.Result.Failure -> {
            emptyMap()
        }
        is Parser.Result.Success -> {
            result.match.value.toMap()
        }
    }

}