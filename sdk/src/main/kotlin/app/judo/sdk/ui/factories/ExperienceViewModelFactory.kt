package app.judo.sdk.ui.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.judo.sdk.core.controllers.current
import app.judo.sdk.core.environment.Environment
import app.judo.sdk.ui.ExperienceViewModel

@Suppress("UNCHECKED_CAST")
internal class ExperienceViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ExperienceViewModel(Environment.current) as T
    }
}