package app.judo.sdk.ui.models

import app.judo.sdk.api.errors.ExperienceError
import app.judo.sdk.api.models.Experience

internal sealed class ExperienceState {
    object Empty : ExperienceState()
    object Loading : ExperienceState()
    data class Error(val error: ExperienceError) : ExperienceState()
    data class Retrieved(val experience: Experience, val screenId: String? = null) : ExperienceState()
}