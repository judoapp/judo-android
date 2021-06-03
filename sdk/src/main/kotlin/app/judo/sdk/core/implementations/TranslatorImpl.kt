package app.judo.sdk.core.implementations

import app.judo.sdk.core.utils.Translator

class TranslatorImpl(
    private val theTranslationMap: Map<String, Map<String, String>>,
    private val theUsersPreferredLanguagesSupplier: () -> List<String>,
) : Translator {

    override fun translate(theTextToTranslate: String): String {

        val theUsersPreferredLanguageTags = theUsersPreferredLanguagesSupplier()

        var result: String? = null

        for (thePreferredLanguageTag in theUsersPreferredLanguageTags) {

            for ((theLanguageTag, theTranslations) in theTranslationMap) {

                if (theLanguageTag == thePreferredLanguageTag) {
                    result = theTranslations[theTextToTranslate]
                    break
                }

                val splitByDash = thePreferredLanguageTag.split('-').first()

                if (theLanguageTag.startsWith(splitByDash)) {
                    result = theTranslations[theTextToTranslate]
                    break
                }

                val splitByUnderscore = thePreferredLanguageTag.split('_').first()

                if (theLanguageTag.startsWith(splitByUnderscore)) {
                    result = theTranslations[theTextToTranslate]
                    break
                }

            }

            if (result != null) break

        }

        return result ?: theTextToTranslate
    }

}