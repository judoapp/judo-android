package app.judo.sdk.core.errors

internal object ErrorMessages {
        const val ACCESS_TOKEN_NOT_BLANK: String = "The accessToken can not be blank"
        const val DOMAIN_NAME_NOT_BLANK: String = "The domainName can not be blank"
        const val DOMAINS_NOT_EMPTY: String = "The list of domains can not be empty"
        const val SDK_NOT_INITIALIZED: String = "The JudoSDK has not been initialized"
        const val EXPERIENCE_NOT_IN_MEMORY: String = "Tried to find a Experience in memory and it was not there"
        const val EXPERIENCE_NOT_FOUND: String = "Server could not find Experience"
        const val UNKNOWN_ERROR: String = "Unknown reason \uD83E\uDD37\u200D"

        @Suppress("FunctionName")
        fun EXTEND_EXPERIENCE_ACTIVITY(activityClass: Class<*>): String {
                return "Class: ${activityClass.canonicalName} does not extend ExperienceActivity."
        }

    }
