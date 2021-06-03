package app.judo.sdk.api.android

import android.content.Intent
import androidx.annotation.MainThread
import app.judo.sdk.ui.ExperienceFragment

fun interface ExperienceFragmentFactory {

    @MainThread
    fun create(intent: Intent): ExperienceFragment

}