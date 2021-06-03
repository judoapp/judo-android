package app.judo.sdk.ui

import androidx.appcompat.app.AppCompatActivity
import app.judo.sdk.api.android.ExperienceFragmentFactory
import app.judo.sdk.core.controllers.current
import app.judo.sdk.core.environment.Environment
import app.judo.sdk.databinding.JudoSdkExperienceActivityLayoutBinding
import app.judo.sdk.ui.extensions.commitNow

open class ExperienceActivity : AppCompatActivity() {

    private lateinit var binding: JudoSdkExperienceActivityLayoutBinding
    private lateinit var fragment: ExperienceFragment

    override fun onStart() {
        super.onStart()
        navigateToExperienceFragment()
    }

    /**
     * Called during [ExperienceActivity.onStart] to show the [ExperienceFragment]
     * provided from the [ExperienceFragmentFactory].
     *
     * Unless overridden any calls after the first will have no effect.
     */
    open fun navigateToExperienceFragment() {
        if (!this::fragment.isInitialized) {

            binding = JudoSdkExperienceActivityLayoutBinding.inflate(layoutInflater)

            setContentView(binding.root)

            fragment = Environment.current
                .experienceFragmentFactory
                .create(intent)

            supportFragmentManager.commitNow {
                setPrimaryNavigationFragment(fragment)
                replace(binding.frame.id, fragment)
            }
        }
    }

}